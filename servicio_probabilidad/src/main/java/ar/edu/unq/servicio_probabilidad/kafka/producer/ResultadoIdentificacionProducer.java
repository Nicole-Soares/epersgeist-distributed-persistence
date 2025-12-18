package ar.edu.unq.servicio_probabilidad.kafka.producer;

import ar.edu.unq.commons.dto.identificacionDTOs.ResultadoIdentificacionDTO;
import com.google.gson.Gson;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResultadoIdentificacionProducer {

    private static final String TOPICO = "resultado_identificacion";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson = new Gson();

    public ResultadoIdentificacionProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarResultado(ResultadoIdentificacionDTO resultado) {
        String json = gson.toJson(resultado);
        kafkaTemplate.send(TOPICO, json);
        System.out.println("[servicio_probabilidad] Envié resultado_identificacion JSON: " + json);
    }
}
