package pl.atipera.githubcrawler.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.atipera.githubcrawler.entity.Branch;
import pl.atipera.githubcrawler.entity.GithubRepo;
import pl.atipera.githubcrawler.entity.GithubRepoDTO;
import pl.atipera.githubcrawler.exception.ResourceNotFoundException;
import pl.atipera.githubcrawler.mapper.GithubRepoToGithubRepoDTOMapper;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
class GithubCrawlerService {
	private static final String API_ROOT = "https://api.github.com";
	private final WebClient webClient;
	private final GithubRepoToGithubRepoDTOMapper mapper;

	public Mono<List<GithubRepoDTO>> getNonForkedRepositories(String username) {
		return webClient.get()
				.uri(API_ROOT + "/users/{username}/repos", username)
				.retrieve()
				.onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.error(new ResourceNotFoundException("User not found")))
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
