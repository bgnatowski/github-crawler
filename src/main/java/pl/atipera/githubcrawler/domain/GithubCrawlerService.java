package pl.atipera.githubcrawler.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.atipera.githubcrawler.exception.ResourceNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class GithubCrawlerService {
	private final RestTemplate restTemplate = new RestTemplate();

	public List<GithubRepo> getNonForkedRepositories(String username) {
		String reposUrl = "https://api.github.com/users/" + username + "/repos";

		GithubRepo[] repos = restTemplate.getForObject(reposUrl, GithubRepo[].class);

		if (repos == null || repos.length == 0) {
			throw new ResourceNotFoundException("User not found.");
		}

		return Arrays.stream(repos)
				.filter(repo -> !repo.isFork())
				.map(this::enrichWithBranches)
				.collect(Collectors.toList());
	}

	private GithubRepo enrichWithBranches(GithubRepo repo) {
		String branchesUrl = "https://api.github.com/repos/" + repo.getOwner().getLogin() + "/" + repo.getName() + "/branches";
		Branch[] branches = restTemplate.getForObject(branchesUrl, Branch[].class);
		repo.setBranches(Arrays.asList(branches));
		return repo;
	}
}
