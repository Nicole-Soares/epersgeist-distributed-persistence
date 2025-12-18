package ar.edu.unq.servicio_temperatura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
public class ServicioTemperaturaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicioTemperaturaApplication.class, args);
    }

}
