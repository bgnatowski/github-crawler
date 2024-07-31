package pl.atipera.githubcrawler.entity;

import java.util.List;

public record GithubRepoDTO(String name,
							GithubUser owner,
							List<Branch> branches) {
}