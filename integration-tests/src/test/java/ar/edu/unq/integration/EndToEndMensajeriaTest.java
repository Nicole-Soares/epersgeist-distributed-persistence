package ar.edu.unq.integration;

import ar.edu.unq.epersgeist.EpersgeistApp;
import ar.edu.unq.commons.dto.MensajeMediumDTO;
import ar.edu.unq.commons.dto.RespuestaEspirituDTO;
import ar.edu.unq.epersgeist.kafka.producer.MensajeMediumProducer;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.TipoUbicacion;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.service.interfaces.EspirituService;
import ar.edu.unq.epersgeist.service.interfaces.MediumService;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import ar.edu.unq.servicio_mensajeria.ServicioMensajeriaApplication;
import ar.edu.unq.servicio_mensajeria.mongo.entity.HistorialSesion;
import ar.edu.unq.servicio_mensajeria.mongo.repository.HistorialSesionRepository;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes =  {
                EpersgeistApp.class,
                ServicioMensajeriaApplication.class
        }
)
@EmbeddedKafka(partitions = 1, topics = {"mensaje_medium", "respuesta_espiritu"})
@TestPropertySource(properties = {
        "KAFKA_BOOTSTRAP=${spring.embedded.kafka.brokers}"
})
@DirtiesContext
public class EndToEndMensajeriaTest extends SharedIntegrationEnvironment {

    @Autowired
    private MensajeMediumProducer mensajeMediumProducer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private MediumService mediumService;

    @Autowired
    private EspirituService espirituService;

    @Autowired
    private HistorialSesionRepository historialSesionRepository;

    @Autowired
    private Neo4jClient neo4jClient;

    private Consumer<String, String> consumerRespuesta;

    private final Gson gson = new Gson();

    private Long mediumId;
    private Long ubicacionId;

    @BeforeEach
    void setUp() {
        Set<Coordenadas> puntos = Set.of(
                new Coordenadas(-34.6, -58.38),
                new Coordenadas(-34.61, -58.38),
                new Coordenadas(-34.61, -58.39)
        );

        Ubicacion ubicacion = new Ubicacion(
                "Ubicación E2E",
                20,
                TipoUbicacion.CEMENTERIO,
                puntos
        );


        Ubicacion creada = ubicacionService.create(ubicacion);
        this.ubicacionId = creada.getId();

        Medium medium = new Medium("Medium E2E",60, 30, creada);
        Medium mediumCreado = mediumService.create(medium);
        this.mediumId = mediumCreado.getId();

        Espiritu espiritu = new Angel("Espiritu E2E",creada,0.8);
        espirituService.create(espiritu);

        neo4jClient.query("""
                CREATE (c:ComunicacionActiva {
                id: randomUUID(),
                mediumId: $mediumId,
                ubicacionId: $ubicacionId,
                espirituId: 999
                })
        """).bind(mediumId).to("mediumId")
                .bind(ubicacionId).to("ubicacionId")
                .run();

        Map<String, Object> props = KafkaTestUtils.consumerProps(
                "end2end-respuesta",
                "true",
                embeddedKafkaBroker
        );

        consumerRespuesta =
                new DefaultKafkaConsumerFactory<String, String>(
                        props,
                        new StringDeserializer(),
                        new StringDeserializer()
                ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(
                consumerRespuesta,
                "respuesta_espiritu"
        );

        historialSesionRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        if (consumerRespuesta != null) {
            consumerRespuesta.close();
        }
    }

    @Test
    void flujoCompletoDesdeMensajeDelMediumHastaRespuestaDelEspiritu() throws InterruptedException {
        // ------ SetUp --------

        MensajeMediumDTO dto = new MensajeMediumDTO(
                mediumId,
                ubicacionId,
                "Hola desde el test E2E"
        );

        // -------- Exercise ----------
        mensajeMediumProducer.enviarMensaje(dto);

        Thread.sleep(1000);

        // espero el mensaje en el topic de respuesta
        ConsumerRecord<String, String> record =
                KafkaTestUtils.getSingleRecord(consumerRespuesta, "respuesta_espiritu");

        // ------ Verify ----------
        assertNotNull(record, "No llegó ningún mensaje al topic respuesta_espiritu");

        // respuesta_espiritu
        RespuestaEspirituDTO respuesta =
                gson.fromJson(record.value(), RespuestaEspirituDTO.class);

        assertEquals(mediumId, respuesta.mediumId());
        assertNotNull(respuesta.respuesta());
        assertFalse(respuesta.respuesta().isBlank());

        // Verificaciones sobre el historial en mongo
        List<HistorialSesion> sesiones = historialSesionRepository.findAll();
        assertFalse(sesiones.isEmpty(), "No se guardó ningún historial de sesión en Mongo");

        HistorialSesion ultima = sesiones.getLast();

        assertEquals(mediumId, ultima.getMediumId());
        assertEquals(ubicacionId, ultima.getUbicacionId());
        assertFalse(ultima.getMensajeMedium().isBlank());
        assertFalse(ultima.getRespuestaEspiritu().isBlank());
    }
}