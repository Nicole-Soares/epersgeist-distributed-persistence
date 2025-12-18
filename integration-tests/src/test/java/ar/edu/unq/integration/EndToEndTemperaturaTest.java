package ar.edu.unq.integration;

import ar.edu.unq.epersgeist.EpersgeistApp;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.TipoUbicacion;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.service.interfaces.EspirituService;
import ar.edu.unq.epersgeist.service.interfaces.TestService;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import ar.edu.unq.servicio_temperatura.ServicioTemperaturaApplication;
import ar.edu.unq.servicio_temperatura.scheduler.TemperaturaScheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@EnableKafka
@SpringBootTest(
        classes = {
                EpersgeistApp.class,
                ServicioTemperaturaApplication.class
        }
)
@ActiveProfiles({"test", "temperatura-e2e"})
@EmbeddedKafka(
        partitions = 1,
        topics = {"temperatura_actualizada"}
)
@TestPropertySource(properties = {
        "KAFKA_BOOTSTRAP=${spring.embedded.kafka.brokers}"
})
@DirtiesContext
class EndToEndTemperaturaTest extends SharedIntegrationEnvironment {

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private EspirituService espirituService;

    @Autowired
    private TestService testService;

    @Autowired
    private TemperaturaScheduler temperaturaScheduler;

    private Long id1;
    private Long id2;

    @BeforeEach
    void setUp() {
        testService.clearAll();

        Ubicacion u1 = new Ubicacion(
                "Ubicacion E2E 1",
                20,
                TipoUbicacion.CEMENTERIO,
                Set.of(
                        new Coordenadas(-34.60, -58.38),
                        new Coordenadas(-34.61, -58.39),
                        new Coordenadas(-34.62, -58.40)
                )
        );
        Ubicacion creada1 = ubicacionService.create(u1);
        id1 = creada1.getId();

        espirituService.create(new Demonio("Sam Winchester", creada1, 60.0));
        espirituService.create(new Angel("Dean Winchester", creada1, 20.0));

        Ubicacion u2 = new Ubicacion(
                "Ubicacion E2E 2",
                20,
                TipoUbicacion.CEMENTERIO,
                Set.of(
                        new Coordenadas(-34.63, -58.41),
                        new Coordenadas(-34.64, -58.42),
                        new Coordenadas(-34.65, -58.43)
                )
        );
        Ubicacion creada2 = ubicacionService.create(u2);
        id2 = creada2.getId();

        espirituService.create(new Demonio("Crowley", creada2, 10.0));
        espirituService.create(new Angel("Anna", creada2, 5.0));

        System.out.println("Se crearon las ubicaciones " + id1 + " y " + id2);
    }

    @Test
    void endToEndTemperaturaGeneradaPorSchedulerYActualizadaEnSQL() throws Exception {

        // Ejecutamos el scheduler manualmente
        temperaturaScheduler.enviarTemperaturaPeriodica();

        await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
            Ubicacion actualizada1 = ubicacionService.findByIdSinConexion(id1);
            Ubicacion actualizada2 = ubicacionService.findByIdSinConexion(id2);

            int temp1 = actualizada1.getTemperatura();
            int temp2 = actualizada2.getTemperatura();

            System.out.println("Temperaturas actualizadas:");
            System.out.println("Ubicación " + id1 + ": " + temp1);
            System.out.println("Ubicación " + id2 + ": " + temp2);

            assertTrue(temp1 >= -10 && temp1 < 20, "Temperatura de ubicación 1 fuera de rango: " + temp1);
            assertTrue(temp2 >= -10 && temp2 < 20, "Temperatura de ubicación 2 fuera de rango: " + temp2);

            assertNotEquals(20, temp1, "Ubicación 1 no fue actualizada por el scheduler");
            assertNotEquals(20, temp2, "Ubicación 2 no fue actualizada por el scheduler");
        });
    }
}
