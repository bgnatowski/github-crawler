package pl.atipera.githubcrawler.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.atipera.githubcrawler.entity.Branch;
import pl.atipera.githubcrawler.entity.GithubRepo;
import pl.atipera.githubcrawler.exception.ResourceNotFoundException;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class GithubCrawlerService {
	private static final String API_ROOT = "https://api.github.com";
	private final RestTemplate restTemplate;
	private final String gitHubToken;

	public List<GithubRepo> getNonForkedRepositories(String username) {
		String reposUrl = API_ROOT + "/users/" + username + "/repos";

		HttpEntity<String> entity = getHttpEntityWithAuthorizationHeader();

		ResponseEntity<GithubRepo[]> response = restTemplate.exchange(reposUrl, HttpMethod.GET, entity, GithubRepo[].class);
		GithubRepo[] repos = response.getBody();

		if (repos == null || repos.length == 0) {
			throw new ResourceNotFoundException("User not found");
		}

		return Arrays.stream(repos)
				.filter(repo -> !repo.isFork())
				.map(this::enrichWithBranches)
				.collect(Collectors.toList());
	}

	private GithubRepo enrichWithBranches(GithubRepo repo) {
		String branchesUrl = API_ROOT + "/repos/" + repo.getOwner().getLogin() + "/" + repo.getName() + "/branches";

		HttpEntity<String> entity = getHttpEntityWithAuthorizationHeader();

		ResponseEntity<Branch[]> response = restTemplate.exchange(branchesUrl, HttpMethod.GET, entity, Branch[].class);
		Branch[] branches = response.getBody();

		repo.setBranches(Arrays.asList(branches));
		return repo;
	}

	private HttpEntity<String> getHttpEntityWithAuthorizationHeader() {
		HttpHeaders headers = new HttpHeaders();
		if(gitHubToken != null && !gitHubToken.isEmpty()){
			headers.set("Authorization", "Bearer " + gitHubToken);
		}
		HttpEntity<String> entity = new HttpEntity<>(headers);
		return entity;
	}
}
