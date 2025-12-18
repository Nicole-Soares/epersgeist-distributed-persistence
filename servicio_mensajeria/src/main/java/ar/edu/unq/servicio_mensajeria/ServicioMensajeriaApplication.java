package ar.edu.unq.servicio_mensajeria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication(scanBasePackages = {
        "ar.edu.unq.servicio_mensajeria",
        "ar.edu.unq.commons"
})
@EnableJpaRepositories
@EnableNeo4jRepositories
@EnableMongoRepositories
public class ServicioMensajeriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioMensajeriaApplication.class, args);
	}
}
