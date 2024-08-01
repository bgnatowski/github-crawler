package pl.atipera.githubcrawler.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import pl.atipera.githubcrawler.mapper.GithubRepoToGithubRepoDTOMapper;

@Configuration
public class GithubCrawlerConfiguration {
	public static final String API_ROOT = "https://api.github.com";

	@Value("${github.token}")
	private String gitHubToken;

	@Bean
	public String gitHubToken() {
		return gitHubToken;
	}

	@Bean
	public WebClient webClient() {
		WebClient.Builder builder = WebClient.builder()
				.baseUrl(API_ROOT);

		if (gitHubToken != null && !gitHubToken.isEmpty()) {
			builder.defaultHeader("Authorization", "Bearer " + gitHubToken);
		}

		return builder.build();
	}

	@Bean
	public GithubRepoToGithubRepoDTOMapper githubRepoToGithubRepoDTOMapper(){
		return new GithubRepoToGithubRepoDTOMapper();
	}

	@Bean
	public GithubCrawlerService githubCrawlerService(WebClient webClient, GithubRepoToGithubRepoDTOMapper githubRepoToGithubRepoDTOMapper) {
		return new GithubCrawlerService(webClient, githubRepoToGithubRepoDTOMapper);
	}

	@Bean
	public GithubCrawlerController githubCrawlerController(GithubCrawlerService githubCrawlerService) {
		return new GithubCrawlerController(githubCrawlerService);
	}
}
