package pl.atipera.githubcrawler.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
class GithubCrawlerConfiguration {
	@Value("${github.token}")
	private String gitHubToken;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public String gitHubToken() {
		return gitHubToken;
	}

	@Bean
	public GithubCrawlerService githubCrawlerService(RestTemplate restTemplate, String gitHubToken) {
		return new GithubCrawlerService(restTemplate, gitHubToken);
	}

	@Bean
	public GithubCrawlerController githubCrawlerController(GithubCrawlerService githubCrawlerService) {
		return new GithubCrawlerController(githubCrawlerService);
	}
}
