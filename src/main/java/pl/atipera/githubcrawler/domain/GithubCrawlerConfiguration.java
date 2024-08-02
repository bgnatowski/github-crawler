package pl.atipera.githubcrawler.domain;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import pl.atipera.githubcrawler.mapper.GithubRepoToGithubRepoDTOMapper;

@Configuration
public class GithubCrawlerConfiguration {
	static final String API_ROOT = "https://api.github.com";

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

		if (StringUtils.isEmpty(gitHubToken)) {
			builder.defaultHeader("Authorization", "Bearer " + gitHubToken);
		}

		return builder.build();
	}

	@Bean
	public GithubRepoToGithubRepoDTOMapper githubRepoToGithubRepoDTOMapper(){
		return new GithubRepoToGithubRepoDTOMapper();
	}
}
