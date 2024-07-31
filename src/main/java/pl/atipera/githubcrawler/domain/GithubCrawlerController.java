package pl.atipera.githubcrawler.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.atipera.githubcrawler.exception.NotAcceptableHeaderException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/githubcrawler")
class GithubCrawlerController {
	private final GithubCrawlerService githubCrawlerService;

	@GetMapping(value = "/user/{username}/repos", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<GithubRepo>> listRepositories(@PathVariable String username,
															 @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String acceptHeader) {
		if(!acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE))
			throw new NotAcceptableHeaderException("Missing 'Accept: applicaiton/json' header");
		List<GithubRepo> repos = githubCrawlerService.getNonForkedRepositories(username);
		return ResponseEntity.ok(repos);
	}

	@GetMapping(value = "/user/{username}/repos/size", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> listRepositoriesSize(@PathVariable String username,
														@RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String acceptHeader) {
		if(!acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE))
			throw new NotAcceptableHeaderException("Missing 'Accept: applicaiton/json' header");
		List<GithubRepo> repos = githubCrawlerService.getNonForkedRepositories(username);
		return ResponseEntity.ok(repos.size());
	}
}
