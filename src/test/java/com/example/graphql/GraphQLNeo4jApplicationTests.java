package com.example.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.neo4j.uri=bolt://localhost:7687",
    "spring.neo4j.authentication.username=neo4j",
    "spring.neo4j.authentication.password=password"
})
class GraphQLNeo4jApplicationTests {

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }
}
