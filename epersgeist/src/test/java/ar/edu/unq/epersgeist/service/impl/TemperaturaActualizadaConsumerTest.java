package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.commons.dto.TemperaturaDTO;
import ar.edu.unq.epersgeist.kafka.consumer.TemperaturaActualizadaConsumer;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.TipoUbicacion;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistence.sql.entity.UbicacionSQL;
import ar.edu.unq.epersgeist.persistence.sql.interfaces.UbicacionSQLDAO;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@EmbeddedKafka(
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        topics = {"temperatura_actualizada"}
)
@DirtiesContext // asegura un único contexto unificado
class TemperaturaActualizadaConsumerTest extends ContainerIntegrationProviderTest{

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private UbicacionSQLDAO ubicacionSQLDAO;

    @Autowired
    private TemperaturaActualizadaConsumer consumer;

    private final Gson gson = new Gson();

    private Long ubicacionId;

    @DynamicPropertySource
    static void kafkaProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.consumer.group-id",
                () -> "test-consumer-" + UUID.randomUUID());
    }

    // ------ SETUP ------
    @BeforeEach
    void prepararUbicacion() {

        Set<Coordenadas> puntos = Set.of(
                new Coordenadas(-34.6, -58.38),
                new Coordenadas(-34.61, -58.38),
                new Coordenadas(-34.61, -58.39)
        );

        Ubicacion ubicacion = new Ubicacion(
                "Ubicacion test",
                20, // temperatura inicial
                TipoUbicacion.CEMENTERIO,
                puntos
        );

        Ubicacion creada = ubicacionService.create(ubicacion);
        this.ubicacionId = creada.getId();

        System.out.println("Temperatura inicial en SQL = " +
                ubicacionSQLDAO.findById(ubicacionId).get().getTemperatura());
    }


    // ------ TEST ------

    @Test
    void cuandoLlegaTemperaturaActualizadaSeGuardaEnUbicacion() {

        TemperaturaDTO dto = new TemperaturaDTO(ubicacionId, 10);
        String json = gson.toJson(dto);

        consumer.consumirTemperatura(json);

        UbicacionSQL sql = ubicacionSQLDAO.findById(ubicacionId).orElseThrow();
        System.out.println("Temperatura en SQL DESPUÉS DEL CONSUMER = " + sql.getTemperatura());

        assertEquals(10, sql.getTemperatura());
    }
}
