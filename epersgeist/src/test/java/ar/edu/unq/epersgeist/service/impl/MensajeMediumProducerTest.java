package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.epersgeist.EpersgeistApp;
import ar.edu.unq.commons.dto.MensajeMediumDTO;
import ar.edu.unq.epersgeist.kafka.producer.MensajeMediumProducer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.assertions.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

@SpringBootTest(classes = EpersgeistApp.class)
@EmbeddedKafka(partitions = 1, topics = {"mensaje_medium"})
@DirtiesContext
public class MensajeMediumProducerTest extends ContainerIntegrationProviderTest{

    @Autowired
    private MensajeMediumProducer producer;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> props = KafkaTestUtils.consumerProps(
                "test-group", "true", embeddedKafkaBroker);

        consumer = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new StringDeserializer()
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "mensaje_medium");
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @DisplayName("enviarMensaje: el productor del mensaje-medium envia este mensaje al topico dentro de kafka")
    void enviarMensajeAlTopicMensajeMedium() {
        // --- Setup ---
        MensajeMediumDTO dto = new MensajeMediumDTO(
                1L,
                10L,
                "Hola espiritu"
        );

        // --- Excercise ---
        producer.enviarMensaje(dto);

        // --- Verify ---
        ConsumerRecord<String, String> record =
                KafkaTestUtils.getSingleRecord(consumer, "mensaje_medium");

        Assertions.assertNotNull(record);
        org.junit.jupiter.api.Assertions.assertTrue(record.value().contains("Hola espiritu"));
        org.junit.jupiter.api.Assertions.assertTrue(record.value().contains("\"mediumId\":1"));
        org.junit.jupiter.api.Assertions.assertTrue(record.value().contains("\"ubicacionId\":10"));
    }
}
