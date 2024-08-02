package pl.atipera.githubcrawler.domain

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import pl.atipera.githubcrawler.entity.Branch
import pl.atipera.githubcrawler.entity.Commit
import pl.atipera.githubcrawler.entity.GithubRepoDTO
import pl.atipera.githubcrawler.entity.GithubUser
import pl.atipera.githubcrawler.exception.ResourceNotFoundException
import reactor.core.publisher.Mono
import spock.lang.Specification

import static org.springframework.http.MediaType.*

@WebFluxTest(controllers = GithubCrawlerController.class)
class GithubCrawlerControllerTest extends Specification {
	@Autowired
	private WebTestClient webClient

	@SpringBean
	private GithubCrawlerService githubCrawlerService = Mock()

	def "should fetch non-forked repositories for a user"() {
		given:
			def repos = getExpectedGithubRepoDTO()
			1 * githubCrawlerService.getAllNonForkedUserRepositories("existing-user") >> Mono.just(repos)

		when:
			def response = webClient.get()
					.uri("/api/githubcrawler/user/existing-user/repos")
					.accept(APPLICATION_JSON)
					.exchange()

		then:
			response.expectStatus().isOk()
			response.expectBody()
					.jsonPath('$[0].name').isEqualTo("repo1")
					.jsonPath('$[1].name').isEqualTo("repo2")
					.jsonPath('$[0].branches.length()').isEqualTo(2)
					.jsonPath('$[1].branches.length()').isEqualTo(1)
	}

	def "should return 404 when user not found"() {
		given:
			githubCrawlerService.getAllNonForkedUserRepositories("non-existent-user") >> Mono.error(new ResourceNotFoundException("User non-existent-user not found"))

		when:
			def response = webClient.get()
					.uri("/api/githubcrawler/user/non-existent-user/repos")
					.accept(APPLICATION_JSON)
					.exchange()

		then:
			response.expectStatus().isNotFound()
			response.expectBody()
					.jsonPath('$.status').isEqualTo(404)
					.jsonPath('$.message').isEqualTo("User non-existent-user not found")
	}

	def "should return 406 with response information when Accept header is missing"() {
		when:
			def response = webClient.get()
					.uri("/api/githubcrawler/user/existing-user/repos")
					.header("Accept", "")
					.exchange()

		then:
			response.expectStatus().isEqualTo(406)
			response.expectBody()
					.jsonPath('$.status').isEqualTo(406)
					.jsonPath('$.message').isEqualTo("Missing 'Accept: application/json' header")
	}

	def "should return 406 when Accept header is invalid"() {
		when:
			def response = webClient.get()
					.uri("/api/githubcrawler/user/existing-user/repos")
					.header("Accept", "text/plain")
					.exchange()

		then:
			response.expectStatus().isEqualTo(406)
	}

	def getExpectedGithubRepoDTO() {
		def commit1 = new Commit("1234")
		def commit2 = new Commit("3456")
		def commit3 = new Commit("7890")

		def branch1 = new Branch("repo1-branch1", commit1)
		def branch2 = new Branch("repo1-branch2", commit2)
		def branch3 = new Branch("repo2-branch1", commit3)

		def repo1 = new GithubRepoDTO("repo1", new GithubUser("testUser"), [branch1, branch2])
		def repo2 = new GithubRepoDTO("repo2", new GithubUser("testUser"), [branch3])
		return [repo1, repo2]
	}
}

