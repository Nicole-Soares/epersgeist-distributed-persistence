package ar.edu.unq.integration;

import org.junit.jupiter.api.TestInstance;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;


@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SharedIntegrationEnvironment {


    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.3")
            .withDatabaseName("epersgeist")
            .withUsername("postgres")
            .withPassword("postgres");


    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.12");


    static Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5.23.0")
            .withLabsPlugins("graph-data-science")
            .withAdminPassword("rootroot")
            .withStartupAttempts(3)
            .withStartupTimeout(Duration.ofMinutes(2));;

    static {
        postgres.start();
        mongo.start();
        neo4j.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);

        r.add("spring.data.mongodb.uri", mongo::getConnectionString);
        r.add("spring.data.mongodb.database", () -> "epersgeist_test");

        r.add("spring.neo4j.uri", neo4j::getBoltUrl);
        r.add("spring.neo4j.authentication.username", () -> "neo4j");
        r.add("spring.neo4j.authentication.password", () -> "rootroot");
    }
}