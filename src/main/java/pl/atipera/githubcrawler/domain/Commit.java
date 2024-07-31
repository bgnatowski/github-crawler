package pl.atipera.githubcrawler.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Commit {
	private String sha;
}
