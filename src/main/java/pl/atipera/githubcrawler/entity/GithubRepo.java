package pl.atipera.githubcrawler.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubRepo {
	private String name;
	private GithubUser owner;
	private List<Branch> branches;
	boolean isFork;
}
