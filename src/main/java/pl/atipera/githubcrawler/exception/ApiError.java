package pl.atipera.githubcrawler.exception;

public record ApiError(
		int status,
		String message
) {
}
