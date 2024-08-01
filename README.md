# GitHub Repository Crawler - Atipera recruitment task application

This application fetches non-forked repositories for a given GitHub user. It uses the GitHub REST API v3 to retrieve repository information, including repository names, owner logins and details about each branch such as the branch name and the last commit SHA.

***Created by***: Bartosz Gnatowski

## API Endpoints
* **Endpoint**: `/api/githubcrawler/user/{username}/repos`
* **Method**: `GET`
* **Headers**:
  * `Accept: application/json (required)`
* **Parameters**:
  * `username`: GitHub username of the user whose repositories you want to fetch`.
* **Responses**:
  * `200 OK`: Successfully retrieved the list of repositories.
  * `404 Not Found`: User does not exist.
  * `406 Not Acceptable`: Missing or incorrect Accept header.

## Response Structure
### Successful response (200 ok)
```json
[
  {
    "name": "repository-name",
    "owner": {
      "login": "owner-login"
    },
    "branches": [
      {
        "name": "branch-name",
        "lastCommitSha": "commit-sha"
      },
      ...
    ]
  },
  ...
]
```

### Error response (404 Not Found)
```json
{
  "status": 404,
  "message": "User {username} not found"
}
```

### Error response (406 Not Acceptable)
```json
{
  "status": 406,
  "message": "Missing 'Accept: application/json' header"
}
```

## Stack: 
* Java 21
* Spring Boot 3.3.2
* Spring WebFlux
* Spock 2.4-M4
* Groovy 4.0
* GitHub API v3

## Getting Started
It is recommended to configure your personal GitHub token in the `github.token` property within the `application.yaml` file (located in the resources directory) to avoid rate limits for the GitHub API v3.

### Instalation
1. Clone repository:
```shell
git clone https://github.com/yourusername/github-repo-crawler.git
cd github-repo-crawler
```
2. Set  the`github.token` property (Optional but Recommended)
   * Log in to https://github.com
   * Navigate to:
     * Profile > Settings > Developer settings > Personal access token > Generate new token
   * Generate Personal Access Token:
     * Set token name, expiration and generate token 
     * Make sure to copy your personal access token
   * Update configuration:
     * Open `src/main/resources/application.yaml`
     * Paste your token as shown below:
```yaml
  github:
    token: "your_token"
```
3. Build project:
```shell
mvn clean install
```
4. Run the application:
```shell
mvn spring-boot:run
```

### Running tests
```shell
mvn test
```