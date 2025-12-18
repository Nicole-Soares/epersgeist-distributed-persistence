package ar.edu.unq.servicio_probabilidad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "ar.edu.unq.servicio_probabilidad",
        "ar.edu.unq.commons"
})
public class ServicioProbabilidadApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioProbabilidadApplication.class, args);
	}

}
