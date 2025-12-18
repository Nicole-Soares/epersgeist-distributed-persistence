package ar.edu.unq.servicio_temperatura.scheduler;


import ar.edu.unq.servicio_temperatura.service.TemperaturaService;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Profile({"temperatura-e2e", "temperatura-real"})
@Component
public class TemperaturaScheduler {

    private final TemperaturaService service;

    public TemperaturaScheduler(TemperaturaService service) {
        this.service = service;
    }

    @Scheduled(fixedRate = 7000)
    public void enviarTemperaturaPeriodica() {
        System.out.println(">>> Enviando temperatura periódica...");
        service.enviarTemperaturasPorHostilidad();
    }
}
