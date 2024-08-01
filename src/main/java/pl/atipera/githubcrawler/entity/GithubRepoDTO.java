package pl.atipera.githubcrawler.entity;

import lombok.Builder;

import java.util.List;

@Builder
public record GithubRepoDTO(String name,
							GithubUser owner,
							List<Branch> branches) {
}