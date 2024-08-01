package pl.atipera.githubcrawler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ApiError> handleException(ResourceNotFoundException e) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NotAcceptableHeaderException.class})
    public ResponseEntity<ApiError> handleException(NotAcceptableHeaderException e) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_ACCEPTABLE.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler({ApiRateLimitExceededException.class})
    public ResponseEntity<ApiError> handleException(ApiRateLimitExceededException e) {
        ApiError apiError = new ApiError(
                HttpStatus.FORBIDDEN.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }
}
