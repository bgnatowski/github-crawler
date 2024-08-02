package pl.atipera.githubcrawler.domain

import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import pl.atipera.githubcrawler.entity.GithubRepoDTO
import pl.atipera.githubcrawler.exception.ResourceNotFoundException
import pl.atipera.githubcrawler.mapper.GithubRepoToGithubRepoDTOMapper
import reactor.core.publisher.Mono
import spock.lang.Specification

class GithubCrawlerServiceTest extends Specification {
	WebClient.Builder webClientBuilder = WebClient.builder()

	def setup() {
		webClientBuilder.exchangeFunction({ clientRequest ->
			def uri = clientRequest.url().toString()
			def response = switch (uri) {
				case "https://api.github.com/users/bgnatowski/repos" -> mockExistUserRepositoriesResponse()
				case "https://api.github.com/repos/bgnatowski/anti-fraud-system/branches" -> mockAntiFraudBranchesResponse()
				case "https://api.github.com/repos/bgnatowski/vue-tv/branches" -> mockVueBranchesResponse()
				case "https://api.github.com/users/not-exist-user-123/repos" -> mockNotExistUserResponse()
				case "https://api.github.com/users/user-with-no-public-repo/repos" -> mockExistUserWithNoRepositoriesResponse()
				default -> null
			}
			return Mono.just(response)
		})
	}

	def "should fetch non-forked repositories with branches for an existing user"() {
		given:
			def githubCrawlerService = new GithubCrawlerService(webClientBuilder.build(), new GithubRepoToGithubRepoDTOMapper())

		when:
			Mono<List<GithubRepoDTO>> result = githubCrawlerService.getAllNonForkedUserRepositories("bgnatowski")

		then:
			result.block() != null
			def responseReposDto = result.block()
			responseReposDto.size() == 2
			responseReposDto[0].name == "anti-fraud-system"
			responseReposDto[1].name == "vue-tv"
			responseReposDto[0].branches.size() == 3
			responseReposDto[1].branches.size() == 3
	}

	def "should return empty array when user exist and has zero public repos"() {
		given:
			def githubCrawlerService = new GithubCrawlerService(webClientBuilder.build(), new GithubRepoToGithubRepoDTOMapper())

		when:
			Mono<List<GithubRepoDTO>> result = githubCrawlerService.getAllNonForkedUserRepositories("user-with-no-public-repo")

		then:
			result.block() != null
			def responseReposDto = result.block()
			responseReposDto.isEmpty()
	}

	def "should throw ResourceNotFoundException and return proper response when user not exist"() {
		given:
			def githubCrawlerService = new GithubCrawlerService(webClientBuilder.build(), new GithubRepoToGithubRepoDTOMapper())

		when:
			githubCrawlerService.getAllNonForkedUserRepositories("not-exist-user-123").block()

		then:
			def e = thrown(ResourceNotFoundException)
			e.message == "User not-exist-user-123 not found"

	}

	def mockExistUserRepositoriesResponse() {
		return ClientResponse.create(HttpStatus.OK)
				.header("Content-Type", "application/json")
				.body("""
    [
        {
            "id": 638141919,
            "name": "anti-fraud-system",
            "full_name": "bgnatowski/anti-fraud-system",
            "owner": {
                "login": "bgnatowski",
                "id": 86551272,
                "node_id": "MDQ6VXNlcjg2NTUxMjcy",
                "avatar_url": "https://avatars.githubusercontent.com/u/86551272?v=4"
            },
            "fork": false,
            "url": "https://api.github.com/repos/bgnatowski/anti-fraud-system",
            "branches_url": "https://api.github.com/repos/bgnatowski/anti-fraud-system/branches{/branch}"
        },
        {
            "id": 782158952,
            "name": "vue-tv",
            "full_name": "bgnatowski/vue-tv",
            "owner": {
                "login": "bgnatowski",
                "id": 86551272,
                "node_id": "MDQ6VXNlcjg2NTUxMjcy",
                "avatar_url": "https://avatars.githubusercontent.com/u/86551272?v=4"
            },
            "fork": false,
            "url": "https://api.github.com/repos/bgnatowski/vue-tv",
            "branches_url": "https://api.github.com/repos/bgnatowski/vue-tv/branches{/branch}"
        }
    ]
    """).build()
	}
	def mockAntiFraudBranchesResponse() {
		return ClientResponse.create(HttpStatus.OK)
				.header("Content-Type", "application/json")
				.body("""
    [
        {
            "name": "anti-fraud-system-v1",
            "commit": {
                "sha": "dd0479b576c6ef7e3f820861f95c2f2382409f99"
            }
        },
        {
            "name": "bank-account",
            "commit": {
                "sha": "fad1cd4cbc04845d6c50aa5baea10aa0970a3341"
            }
        },
        {
            "name": "main",
            "commit": {
                "sha": "50aa3aa63413a6e4817958ea328807df5875c34d"
            }
        }
    ]
    """).build()
	}

	def mockVueBranchesResponse() {
		return ClientResponse.create(HttpStatus.OK)
				.header("Content-Type", "application/json")
				.body("""
    [
        {
            "name": "feat-02-bartek",
            "commit": {
                "sha": "65b5f27a390454795b5923428f71680a2d8bf0f0",
                "url": "https://api.github.com/repos/bgnatowski/vue-tv/commits/65b5f27a390454795b5923428f71680a2d8bf0f0"
            },
            "protected": false
        },
        {
            "name": "hotfix",
            "commit": {
                "sha": "e1a9d257caf363cdd5f3b62e23c71751d31c23c9",
                "url": "https://api.github.com/repos/bgnatowski/vue-tv/commits/e1a9d257caf363cdd5f3b62e23c71751d31c23c9"
            },
            "protected": false
        },
        {
            "name": "main",
            "commit": {
                "sha": "45e66e70772283a06f4f2cb5cf1bcfcecbf39a62",
                "url": "https://api.github.com/repos/bgnatowski/vue-tv/commits/45e66e70772283a06f4f2cb5cf1bcfcecbf39a62"
            },
            "protected": true
        }
    ]
    """).build()
	}

	def mockExistUserWithNoRepositoriesResponse() {
		return ClientResponse.create(HttpStatus.OK)
				.header("Content-Type", "application/json")
				.body("[]").build()
	}

	def mockNotExistUserResponse() {
		return ClientResponse.create(HttpStatus.NOT_FOUND)
				.header("Content-Type", "application/json")
				.body("""
    {
    "message": "Not Found",
    "documentation_url": "https://docs.github.com/rest/repos/repos#list-repositories-for-a-user",
    "status": "404"
}
""").build()
	}
}
