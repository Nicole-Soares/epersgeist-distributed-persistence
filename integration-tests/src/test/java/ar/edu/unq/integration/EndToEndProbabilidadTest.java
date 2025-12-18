package ar.edu.unq.integration;

import ar.edu.unq.commons.neo.entity.ComunicacionActiva;
import ar.edu.unq.epersgeist.EpersgeistApp;
import ar.edu.unq.commons.dto.identificacionDTOs.ResultadoIdentificacionDTO;
import ar.edu.unq.commons.dto.identificacionDTOs.SolicitudIdentificacionDTO;
import ar.edu.unq.epersgeist.kafka.producer.SolicitudIdentificacionProducer;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.TipoUbicacion;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistence.neo.ComunicacionActivaNeo4JDao;
import ar.edu.unq.epersgeist.service.interfaces.EspirituService;
import ar.edu.unq.epersgeist.service.interfaces.MediumService;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import ar.edu.unq.servicio_probabilidad.ServicioProbabilidadApplication;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.Set;

@SpringBootTest(classes = {EpersgeistApp.class, ServicioProbabilidadApplication.class})
@EmbeddedKafka(partitions = 1, topics = {"solicitud_identificacion", "resultado_identificacion"})
@TestPropertySource(properties = {
        "KAFKA_BOOTSTRAP=${spring.embedded.kafka.brokers}"
})
@DirtiesContext
public class EndToEndProbabilidadTest extends SharedIntegrationEnvironment {

    @Autowired
    private SolicitudIdentificacionProducer solicitudIdentificacionProducer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private EspirituService espirituService;

    @Autowired
    private MediumService mediumService;

    @Autowired
    private ComunicacionActivaNeo4JDao comunicacionActivaNeo4JDao;

    private Consumer<String, String> consumerResultado;
    private final Gson gson = new Gson();

    private Set<Coordenadas> coordenadasSet;
    private Ubicacion ubicacion;
    private Medium medium;
    private Espiritu espiritu;

    @BeforeEach
    void setUp() {
        Map<String, Object> props = KafkaTestUtils.consumerProps(
                "end2end-probabilidad", "true", embeddedKafkaBroker);

        consumerResultado = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new StringDeserializer()
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumerResultado, "resultado_identificacion");


        coordenadasSet = Set.of(new Coordenadas(0.0, 0.0), new Coordenadas(0.0, 1.0), new Coordenadas(1.0, 0.0));
        ubicacion = new Ubicacion("Greenpath", 50, TipoUbicacion.CEMENTERIO, coordenadasSet);
        espiritu = new Demonio("Grimm", ubicacion, 20.0);
        medium = new Medium("Hornet", 420, 69, ubicacion);

        ubicacionService.create(ubicacion);
        espirituService.create(espiritu);
        mediumService.create(medium);

        ComunicacionActiva comunicacion = new ComunicacionActiva(
                medium.getId(),
                ubicacion.getId(),
                espiritu.getId()
        );

        comunicacionActivaNeo4JDao.save(comunicacion);
    }

    @AfterEach
    void tearDown() {
        consumerResultado.close();
    }

    @Test
    void flujoCompletoSolicitudIdentificacionHastaResultado() {
        // --- Setup ---
        SolicitudIdentificacionDTO.MediumConjetura conjetura = new SolicitudIdentificacionDTO.MediumConjetura("Raul");

        // --- Exercise ---
        solicitudIdentificacionProducer.enviarSolicitud(medium.getId(), conjetura);

        ConsumerRecord<String, String> registroResultado =
                KafkaTestUtils.getSingleRecord(consumerResultado, "resultado_identificacion");

        Assertions.assertNotNull(registroResultado);

        ResultadoIdentificacionDTO resultado =
                gson.fromJson(registroResultado.value(), ResultadoIdentificacionDTO.class);

        // --- Verify ---
        Assertions.assertEquals(medium.getId(), resultado.mediumId());
        Assertions.assertEquals(espiritu.getId(), resultado.espirituId());
        Assertions.assertNotNull(resultado.mensaje());
        Assertions.assertTrue(resultado.mensaje().contains("El espíritu"));
    }

}