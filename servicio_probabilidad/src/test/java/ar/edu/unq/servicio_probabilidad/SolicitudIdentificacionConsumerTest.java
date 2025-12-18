package ar.edu.unq.servicio_probabilidad;

import ar.edu.unq.commons.dto.identificacionDTOs.ResultadoIdentificacionDTO;
import ar.edu.unq.commons.dto.identificacionDTOs.SolicitudIdentificacionDTO;
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
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

@SpringBootTest(classes = ServicioProbabilidadApplication.class)
@EmbeddedKafka(partitions = 1, topics = {"solicitud_identificacion", "resultado_identificacion"})
@TestPropertySource(properties = {
        "KAFKA_BOOTSTRAP=${spring.embedded.kafka.brokers}"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SolicitudIdentificacionConsumerTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, String> consumerResultado;
    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        Map<String, Object> props = KafkaTestUtils.consumerProps(
                "test-grupo-resultado", "true", embeddedKafkaBroker);

        consumerResultado = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new StringDeserializer()
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumerResultado, "resultado_identificacion");
    }

    @AfterEach
    void tearDown() {
        if(consumerResultado != null) {
            consumerResultado.unsubscribe();
            consumerResultado.close();
        }
    }

    @Test
    @DisplayName("Cuando la identificacion es exitosa, se publica el resultado con exito en el topico resultado_identificacion")
    void cuandoIdentificacionEsExitosaSePublicaResultadoExito() {
        // ---------------- Setup ----------------

        SolicitudIdentificacionDTO dto = new SolicitudIdentificacionDTO(
                1L,
                1, // cordura alta para asegurar exito
                new SolicitudIdentificacionDTO.Real(
                        3L,
                        "espirituTest",
                        "tipoTest",
                        0
                ), // hostilidad baja y afinidad alta para asegurar exito
                new SolicitudIdentificacionDTO.MediumConjetura(
                        "espirituTest"
                ) // Mismo nombre para asegurar exito
        );

        // --------------- Exercise ---------------

        String json = gson.toJson(dto);

        kafkaTemplate.send("solicitud_identificacion", json);

        ConsumerRecord<String, String> record =
                KafkaTestUtils.getSingleRecord(consumerResultado, "resultado_identificacion");

        // --------------- Verify ----------------
        Assertions.assertNotNull(record);
        ResultadoIdentificacionDTO resultado =
                gson.fromJson(record.value(), ResultadoIdentificacionDTO.class);

        Assertions.assertTrue(resultado.exito());
        Assertions.assertEquals(3L, resultado.espirituId());
        Assertions.assertEquals(1L, resultado.mediumId());
        Assertions.assertEquals("¡Éxito! El espíritu ha sido identificado.", resultado.mensaje());
    }


    @Test
    @DisplayName("Cuando la identificacion no es exitosa, se publica el resultado con fracaso en el topico resultado_identificacion")
    void cuandoIdentificacionNoEsExitosaSePublicaResultadoFracaso() {
        // ---------------- Setup ----------------

        SolicitudIdentificacionDTO dto = new SolicitudIdentificacionDTO(
                1L,
                0, // cordura baja para asegurar fracaso
                new SolicitudIdentificacionDTO.Real(
                        3L,
                        "espirituTest",
                        "tipoTest",
                        1
                ), // hostilidad alta y afinidad baja para asegurar fracaso
                new SolicitudIdentificacionDTO.MediumConjetura(
                        "Alan"
                ) // Nombre distinto para asegurar fracaso
        );

        // --------------- Exercise ---------------

        String json = gson.toJson(dto);

        kafkaTemplate.send("solicitud_identificacion", json);

        ConsumerRecord<String, String> record =
                KafkaTestUtils.getSingleRecord(consumerResultado, "resultado_identificacion");

        // --------------- Verify ----------------
        Assertions.assertNotNull(record);
        ResultadoIdentificacionDTO resultado =
                gson.fromJson(record.value(), ResultadoIdentificacionDTO.class);

        Assertions.assertFalse(resultado.exito());
        Assertions.assertEquals(3L, resultado.espirituId());
        Assertions.assertEquals(1L, resultado.mediumId());
        Assertions.assertEquals("Fracaso. El espíritu no ha sido identificado.", resultado.mensaje());
    }



}