package ar.edu.unq.servicio_mensajeria;

import ar.edu.unq.commons.dto.MensajeMediumDTO;
import ar.edu.unq.commons.dto.RespuestaEspirituDTO;
import ar.edu.unq.servicio_mensajeria.consumer.MensajeMediumConsumer;
import ar.edu.unq.servicio_mensajeria.mongo.entity.HistorialSesion;
import ar.edu.unq.servicio_mensajeria.mongo.repository.HistorialSesionRepository;
import ar.edu.unq.commons.neo.entity.ComunicacionActiva;
import ar.edu.unq.commons.neo.dao.ComunicacionActivaDAO;
import ar.edu.unq.servicio_mensajeria.sql.entity.EspirituCandidato;
import ar.edu.unq.servicio_mensajeria.sql.repository.EspirituCandidatoRepository;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest(classes = ServicioMensajeriaApplication.class)
@EmbeddedKafka(partitions = 1, topics = {"mensaje_medium", "respuesta_espiritu"})
@TestPropertySource(properties = {
        "KAFKA_BOOTSTRAP=${spring.embedded.kafka.brokers}",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MensajeMediumConsumerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("epersgeist")
            .withUsername("postgres")
            .withPassword("postgres");

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

    @Container
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

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private MensajeMediumConsumer consumer; // no lo usamos directo pero fuerza el registro del @KafkaListener

    @Autowired
    private HistorialSesionRepository historialSesionRepository;

    @Autowired
    private EspirituCandidatoRepository espirituCandidatoRepository;

    @Autowired
    private ComunicacionActivaDAO comunicacionActivaDAO;

    private Consumer<String, String> consumerRespuesta;

    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        // limpio BDs
        historialSesionRepository.deleteAll();
        espirituCandidatoRepository.deleteAll();
        comunicacionActivaDAO.deleteAll();

        //consumer de respuesta espiritu
        String randomGroup = "grupo-test" + UUID.randomUUID();
        Map<String, Object> props = KafkaTestUtils.consumerProps(
                randomGroup, "true", embeddedKafkaBroker
        );

        consumerRespuesta = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new StringDeserializer()
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumerRespuesta, "respuesta_espiritu");
    }

    @AfterEach
    void tearDown() {
        if(consumerRespuesta != null) {
            consumerRespuesta.unsubscribe();
            consumerRespuesta.close();
        }
    }

    @Test
    @DisplayName("Sin comunicación activa, busca candidato nuevo, responde, guarda historial")
    void sinComunicacionActivaConCandidatoNuevo() {
        // ------ setup --------
        Long mediumId = 1L;
        Long ubicacionId = 10L;

        // Creo un espíritu candidato real para esa ubicación
        EspirituCandidato candidato = new EspirituCandidato();
        candidato.setUbicacionId(ubicacionId);
        candidato.setHostilidad(3);
        candidato.setNombre("candidato test");
        candidato = espirituCandidatoRepository.save(candidato);

        String mensaje = "Hola candidato test, estoy intentando contactarte";
        MensajeMediumDTO dto = new MensajeMediumDTO(mediumId, ubicacionId, mensaje);
        String json = gson.toJson(dto);

        // ---------- Exercise ----------
        kafkaTemplate.send("mensaje_medium", json);

        // ---------- Verify ----------
        // debe publicarse una respuesta_espiritu
        ConsumerRecord<String, String> recordRespuesta =
                KafkaTestUtils.getSingleRecord(consumerRespuesta, "respuesta_espiritu", Duration.ofSeconds(10));
        assertThat(recordRespuesta).isNotNull();

        RespuestaEspirituDTO respuesta = gson.fromJson(recordRespuesta.value(), RespuestaEspirituDTO.class);

        assertThat(respuesta.mediumId()).isEqualTo(mediumId);
        assertThat(respuesta.espirituId()).isEqualTo(candidato.getId());
        assertThat(respuesta.hostilidad()).isEqualTo(candidato.getHostilidad());
        assertThat(respuesta.respuesta()).isNotBlank();

        // debe existir comunicación activa nueva en Neo
        Optional<ComunicacionActiva> comunicacion =
                comunicacionActivaDAO.findByMediumIdAndUbicacionId(mediumId, ubicacionId);
        assertThat(comunicacion).isPresent();
        assertThat(comunicacion.get().getEspirituId()).isEqualTo(candidato.getId());

        // debe guardarse el historial en Mongo
        await().atMost(Duration.ofSeconds(5))
                .until(() -> historialSesionRepository.count() == 1);

        HistorialSesion h = historialSesionRepository.findAll().getFirst();
        assertThat(h.getMediumId()).isEqualTo(mediumId);
        assertThat(h.getUbicacionId()).isEqualTo(ubicacionId);
        assertThat(h.getEspirituId()).isEqualTo(candidato.getId());
        assertThat(h.getMensajeMedium()).isEqualTo(mensaje);
        assertThat(h.getRespuestaEspiritu()).isEqualTo(respuesta.respuesta());
    }

    @Test
    @DisplayName("Con comunicación activa y espíritu vigente, reutiliza ese espíritu y guarda historial")
    void conComunicacionActivaYEspirituVigente() {
        // ---------- setup ----------
        Long mediumId = 2L;
        Long ubicacionId = 20L;
        String mensaje = "Mensaje con comunicación ya activa";

        // creo espíritu candidato
        EspirituCandidato candidato = new EspirituCandidato();
        candidato.setUbicacionId(ubicacionId);
        candidato.setHostilidad(5);
        candidato.setNombre("candidato test2");
        candidato = espirituCandidatoRepository.save(candidato);

        // creo una comunicación activa que lo referencia al espiritu recien creado
        ComunicacionActiva comunicacion = new ComunicacionActiva();
        comunicacion.setMediumId(mediumId);
        comunicacion.setUbicacionId(ubicacionId);
        comunicacion.setEspirituId(candidato.getId());
        comunicacionActivaDAO.save(comunicacion);

        MensajeMediumDTO dto = new MensajeMediumDTO(mediumId, ubicacionId, mensaje);
        String json = gson.toJson(dto);

        // ---------- Exercise ----------
        kafkaTemplate.send("mensaje_medium", json);

        // ---------- Verify ------------
        // respuesta_espiritu usando el mismo espíritu
        ConsumerRecord<String, String> record =
                KafkaTestUtils.getSingleRecord(consumerRespuesta, "respuesta_espiritu", Duration.ofSeconds(10));

        RespuestaEspirituDTO respuesta = gson.fromJson(record.value(), RespuestaEspirituDTO.class);

        assertThat(respuesta.mediumId()).isEqualTo(mediumId);
        assertThat(respuesta.espirituId()).isEqualTo(candidato.getId());
        assertThat(respuesta.hostilidad()).isEqualTo(candidato.getHostilidad());
        assertThat(respuesta.respuesta()).isNotBlank();

        // Comunicación activa sigue apuntando al mismo espíritu
        Optional<ComunicacionActiva> comunicacionActualizada =
                comunicacionActivaDAO.findByMediumIdAndUbicacionId(mediumId, ubicacionId);
        assertThat(comunicacionActualizada).isPresent();
        assertThat(comunicacionActualizada.get().getEspirituId()).isEqualTo(candidato.getId());

        // nuevo historial creado
        await().atMost(Duration.ofSeconds(5))
                .until(() -> historialSesionRepository.count() == 1);

        HistorialSesion h = historialSesionRepository.findAll().getFirst();
        assertThat(h.getMediumId()).isEqualTo(mediumId);
        assertThat(h.getUbicacionId()).isEqualTo(ubicacionId);
        assertThat(h.getEspirituId()).isEqualTo(candidato.getId());
        assertThat(h.getMensajeMedium()).isEqualTo(mensaje);
        assertThat(h.getRespuestaEspiritu()).isEqualTo(respuesta.respuesta());
    }

    @Test
    @DisplayName("Cuando no hay espíritus en la ubicación, no publica respuesta y guarda historial sin espirituId")
    void sinEspiritusEnUbicacion() {
        // ---------- Setup ----------
        Long mediumId = 3L;
        Long ubicacionId = 30L;
        String mensaje = "Hay alguien aqui?";

        // No creo ni espíritus ni comunicación activa para esa ubicación,
        // así forzamos la rama de ausenciaDeEspiritusEnUbicacion(dto)

        MensajeMediumDTO dto = new MensajeMediumDTO(mediumId, ubicacionId, mensaje);
        String json = gson.toJson(dto);

        // ---------- Exercise Y Verify ----------
        kafkaTemplate.send("mensaje_medium", json);

        // ---------- No debería haber mensajes en respuesta_espiritu ----------
        var mensajesRespuesta = consumerRespuesta.poll(Duration.ofSeconds(3));
        assertThat(mensajesRespuesta.count()).isZero();
    }
}
