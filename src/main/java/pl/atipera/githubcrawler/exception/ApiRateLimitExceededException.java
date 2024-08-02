package pl.atipera.githubcrawler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.*;

@ResponseStatus(code= NOT_FOUND)
public class ApiRateLimitExceededException extends RuntimeException {
	public ApiRateLimitExceededException(String message) {
		super(message);
	}
}
