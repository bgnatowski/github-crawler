package pl.atipera.githubcrawler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND)
public class ApiRateLimitExceededException extends RuntimeException {
	public ApiRateLimitExceededException(String message) {
		super(message);
	}
}
