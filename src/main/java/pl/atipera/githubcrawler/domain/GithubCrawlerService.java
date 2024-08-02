package pl.atipera.githubcrawler.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.atipera.githubcrawler.entity.Branch;
import pl.atipera.githubcrawler.entity.GithubRepo;
import pl.atipera.githubcrawler.entity.GithubRepoDTO;
import pl.atipera.githubcrawler.exception.ApiRateLimitExceededException;
import pl.atipera.githubcrawler.exception.ResourceNotFoundException;
import pl.atipera.githubcrawler.mapper.GithubRepoToGithubRepoDTOMapper;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static pl.atipera.githubcrawler.domain.GithubCrawlerConfiguration.API_ROOT;

@Service
@RequiredArgsConstructor
public class GithubCrawlerService {
	private final WebClient webClient;
	private final GithubRepoToGithubRepoDTOMapper mapper;

	public Mono<List<GithubRepoDTO>> getAllNonForkedUserRepositories(String username) {
		return webClient.get()
				.uri(API_ROOT + "/users/{username}/repos", username)
				.retrieve()
				.onStatus(NOT_FOUND::equals, clientResponse -> Mono.error(new ResourceNotFoundException("User %s not found".formatted(username))))
				.onStatus(FORBIDDEN::equals, clientResponse -> Mono.error(new ApiRateLimitExceededException("Github API rate limit exceeded. Setup personal access token as property in application.yaml")))
				.bodyToFlux(GithubRepo.class)
				.filter(repo -> !repo.isFork())
				.flatMap(repo -> enrichWithBranches(repo).flux())
				.map(mapper)
				.sort(Comparator.comparing(GithubRepoDTO::name))
				.collectList();
	}

	private Mono<GithubRepo> enrichWithBranches(GithubRepo repo) {
		return webClient.get()
				.uri(API_ROOT + "/repos/{owner}/{repo}/branches", repo.getOwner().getLogin(), repo.getName())
				.retrieve()
				.bodyToFlux(Branch.class)
				.collectList()
				.map(branches -> {
					repo.setBranches(branches);
					return repo;
				});
	}
}
