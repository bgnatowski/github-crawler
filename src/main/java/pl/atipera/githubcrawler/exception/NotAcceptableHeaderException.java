package pl.atipera.githubcrawler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class NotAcceptableHeaderException extends RuntimeException {
	public NotAcceptableHeaderException(String message) {
		super(message);
	}
}
