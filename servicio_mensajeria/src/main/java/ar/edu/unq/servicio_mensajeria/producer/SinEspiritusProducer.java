package ar.edu.unq.servicio_mensajeria.producer;

import ar.edu.unq.commons.dto.SinEspiritusEnUbicacionDTO;
import com.google.gson.Gson;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SinEspiritusProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson = new Gson();

    //topico de aviso de ausencia de espiritus en ubicacion.
    private static final String TOPIC_SIN_ESPIRITUS = "sin_espiritus_en_ubicacion";

    public SinEspiritusProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarEventoSinEspiritus(Long ubicacionId) {
        SinEspiritusEnUbicacionDTO dto = new SinEspiritusEnUbicacionDTO(ubicacionId);
        String json = gson.toJson(dto);
        System.out.println("[servicio_mensajeria] Enviando evento sin espiritus para ubicacion " + ubicacionId);
        kafkaTemplate.send(TOPIC_SIN_ESPIRITUS, json);
    }
}
