package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.epersgeist.EpersgeistApp;
import ar.edu.unq.commons.dto.identificacionDTOs.ResultadoIdentificacionDTO;
import ar.edu.unq.epersgeist.kafka.consumer.ResultadoIdentificacionConsumer;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.TipoUbicacion;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CalculadorDeDistancia;
import ar.edu.unq.epersgeist.service.interfaces.EspirituService;
import ar.edu.unq.epersgeist.service.interfaces.MediumService;
import ar.edu.unq.epersgeist.service.interfaces.TestService;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import com.google.gson.Gson;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;
import java.util.UUID;

@SpringBootTest(classes = EpersgeistApp.class)
@Testcontainers
@EmbeddedKafka(
        partitions = 1,
        topics = {"resultado_identificacion"}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ResultadoIdentificacionConsumerTest extends ContainerIntegrationProviderTest{

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        // KAFKA - evitar cacheadas de offsets
        registry.add("spring.kafka.consumer.group-id",
                () -> "test-" + UUID.randomUUID());
    }

    @Autowired
    private ResultadoIdentificacionConsumer consumer;

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private EspirituService espirituService;

    @Autowired
    private MediumService mediumService;

    @Autowired
    private TestService testService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final Gson gson = new Gson();

    private Set<Coordenadas> coordenadasSet;
    private Ubicacion ubicacion;
    private Medium medium;
    private Espiritu espiritu;
    private Long mediumId;
    private Long espirituId;


    @BeforeEach
    void setUp() {

        coordenadasSet = Set.of(new Coordenadas(0.0, 0.0), new Coordenadas(0.0, 1.0), new Coordenadas(1.0, 0.0));
        ubicacion = new Ubicacion("Greenpath", 50, TipoUbicacion.CEMENTERIO, coordenadasSet);
        espiritu = new Demonio("Grimm", ubicacion, 20.0);
        medium = new Medium("Hornet", 420, 69, ubicacion);

        Ubicacion ubicacionCreada = ubicacionService.create(ubicacion);
        Espiritu espirituCreado = espirituService.create(espiritu);
        Medium mediumCreado = mediumService.create(medium);

        espirituId = espirituCreado.getId();
        mediumId = mediumCreado.getId();

    }

    @AfterEach
    void tearDown() {
        testService.clearAll();
        mongoTemplate.remove(new Query(), CalculadorDeDistancia.class);
    }

    @Test
    @DisplayName("Cuando se recibe una identificacion exitosa se borra al espiritu")
    void cuandoSeRecibeIdentificacionExitosaSeBorraEspiritu() {
        // --- Setup ---
        ResultadoIdentificacionDTO dto = new ResultadoIdentificacionDTO(
                true,
                false,
                "¡Éxito! El espíritu ha sido liberado.",
                espirituId,
                mediumId
        );
        String mensajeJson = gson.toJson(dto);

        // --- Excercise ---
        consumer.consumirMensaje(mensajeJson);

        try { Thread.sleep(10000); } catch (InterruptedException e) { /* Esperar a que el consumidor procese el mensaje */ }

        // --- Verify ---
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            espirituService.findById(espirituId);
        });

        // El medium no debe ser eliminado
        Assertions.assertNotNull(mediumService.findById(mediumId));
    }


    @Test
    @DisplayName("Cuando se recibe una identificacion no exitosa y que debe morir, se borra al medium")
    void cuandoSeRecibeIdentificacionNoExitosaSeBorraMedium() {
        // --- Setup ---
        ResultadoIdentificacionDTO dto = new ResultadoIdentificacionDTO(
                false,
                true,
                "¡Fracaso! El espíritu permanece atrapado.",
                espirituId,
                mediumId
        );
        String mensajeJson = gson.toJson(dto);

        // --- Excercise ---
        consumer.consumirMensaje(mensajeJson);

        try { Thread.sleep(10000); } catch (InterruptedException e) { /* Esperar a que el consumidor procese el mensaje */ }

        // --- Verify ---
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            mediumService.findById(mediumId);
        });

        // El espíritu no debe ser eliminado
        Assertions.assertNotNull(espirituService.findById(espirituId));
    }

    @Test
    @DisplayName("Cuando se recibe una identificacion no exitosa y que no debe morir, se le resta 50 puntos de cordura")
    void cuandoSeRecibeIdentificacionNoExitosaSeRestaCorduraAlMedium() {
        // --- Setup ---
        ResultadoIdentificacionDTO dto = new ResultadoIdentificacionDTO(
                false,
                false,
                "¡Fracaso! El espíritu permanece atrapado.",
                espirituId,
                mediumId
        );
        String mensajeJson = gson.toJson(dto);

        // --- Excercise ---
        consumer.consumirMensaje(mensajeJson);

        try { Thread.sleep(10000); } catch (InterruptedException e) { /* Esperar a que el consumidor procese el mensaje */ }

        // --- Verify ---
        // Tanto el medium como el espiritu no deben ser eliminados
        Assertions.assertNotNull(mediumService.findById(mediumId));
        Assertions.assertNotNull(espirituService.findById(espirituId));
        Assertions.assertEquals(50.0, mediumService.findById(mediumId).getCordura());
    }
}
