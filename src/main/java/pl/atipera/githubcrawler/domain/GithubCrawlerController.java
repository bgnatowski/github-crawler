package pl.atipera.githubcrawler.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.atipera.githubcrawler.entity.GithubRepoDTO;
import pl.atipera.githubcrawler.exception.NotAcceptableHeaderException;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.MediaType.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/githubcrawler")
public class GithubCrawlerController {
	private final GithubCrawlerService githubCrawlerService;

	@GetMapping(value = "/user/{username}/repos", produces = APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<List<GithubRepoDTO>>> listRepositories(@PathVariable String username,
																	  @RequestHeader(value = "Accept") String acceptHeader) {

		if (!acceptHeader.contains(APPLICATION_JSON_VALUE))
			throw new NotAcceptableHeaderException("Missing 'Accept: application/json' header");

		return githubCrawlerService.getAllNonForkedUserRepositories(username)
				.map(ResponseEntity::ok);
	}
}
