package ar.edu.unq.commons.neo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@Configuration
@EnableNeo4jRepositories(basePackages = "ar.edu.unq.commons.neo.dao")
public class CommonsNeo4jConfig {}
