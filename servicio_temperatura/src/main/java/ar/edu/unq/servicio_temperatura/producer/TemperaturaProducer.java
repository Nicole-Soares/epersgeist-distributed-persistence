package ar.edu.unq.servicio_temperatura.producer;

import ar.edu.unq.commons.dto.TemperaturaDTO;
import com.google.gson.Gson;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TemperaturaProducer {

    private static final String TOPIC = "temperatura_actualizada";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson = new Gson();

    public TemperaturaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarTemperatura(TemperaturaDTO temperaturaDTO) {
        String json = gson.toJson(temperaturaDTO);
        kafkaTemplate.send(TOPIC, json);
        System.out.println("Temperatura actual: " + json);
    }
}
