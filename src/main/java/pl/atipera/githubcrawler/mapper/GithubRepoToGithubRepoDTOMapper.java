package pl.atipera.githubcrawler.mapper;

import pl.atipera.githubcrawler.entity.GithubRepo;
import pl.atipera.githubcrawler.entity.GithubRepoDTO;

import java.util.function.Function;

public class GithubRepoToGithubRepoDTOMapper implements Function<GithubRepo, GithubRepoDTO> {
	@Override
	public GithubRepoDTO apply(GithubRepo githubRepo) {
		return new GithubRepoDTO(
				githubRepo.getName(),
				githubRepo.getOwner(),
				githubRepo.getBranches()
		);
	}
}
