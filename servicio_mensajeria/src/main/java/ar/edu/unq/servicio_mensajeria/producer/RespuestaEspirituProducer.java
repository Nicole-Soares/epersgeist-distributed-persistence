package ar.edu.unq.servicio_mensajeria.producer;

import ar.edu.unq.commons.dto.RespuestaEspirituDTO;
import com.google.gson.Gson;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RespuestaEspirituProducer {

    private static final String TOPICO = "respuesta_espiritu";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson = new Gson();

    public RespuestaEspirituProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarRespuesta(RespuestaEspirituDTO dto) {
        String json = gson.toJson(dto);
        kafkaTemplate.send(TOPICO, json);
        System.out.println("[servicio_mensajeria] Envié respuesta_espiritu JSON: " + json);
    }
}
