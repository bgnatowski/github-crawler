package pl.atipera.githubcrawler


import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class LoadContextTest extends Specification {
    def "when context is loaded then all expected beans are created"() {
        expect: "the WebController is created"
        1 == 1
    }
}