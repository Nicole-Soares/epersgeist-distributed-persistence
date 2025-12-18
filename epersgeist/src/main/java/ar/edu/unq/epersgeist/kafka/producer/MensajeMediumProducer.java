package ar.edu.unq.epersgeist.kafka.producer;

import ar.edu.unq.commons.dto.MensajeMediumDTO;
import com.google.gson.Gson;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MensajeMediumProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    //liberia que permite pasar los objetos a json
    private final Gson gson = new Gson();

    public MensajeMediumProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarMensaje(MensajeMediumDTO dto) {
        // Convertimos el objeto a json
        String json = gson.toJson(dto);

        // Enviamos el JSON al topic mensaje_medium
        kafkaTemplate.send("mensaje_medium", json);

        System.out.println("Enviado a Kafka (mensaje_medium): " + json);
    }
}
