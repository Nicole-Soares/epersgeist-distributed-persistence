package ar.edu.unq.epersgeist.service.impl;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ContainerIntegrationProviderTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.3")
            .withDatabaseName("epersgeist_test")
            .withUsername("test")
            .withPassword("test");

    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.12");

    static Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5.23.0")
            .withAdminPassword("rootroot")
            .withLabsPlugins("graph-data-science")
            .withStartupAttempts(3)
            .withStartupTimeout(Duration.ofMinutes(2));

    static {
        postgres.start();
        mongo.start();
        neo4j.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // Postgres
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        // Mongo
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "epersgeist_test");

        // Neo4j
        registry.add("spring.neo4j.uri", neo4j::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> "rootroot");
    }
}