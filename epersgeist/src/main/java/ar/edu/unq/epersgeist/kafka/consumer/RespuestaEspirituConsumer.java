package ar.edu.unq.epersgeist.kafka.consumer;

import ar.edu.unq.commons.dto.RespuestaEspirituDTO;
import ar.edu.unq.epersgeist.service.interfaces.MediumService;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RespuestaEspirituConsumer {

    private final MediumService mediumService;
    private final Gson gson = new Gson();

    public RespuestaEspirituConsumer(MediumService mediumService) {
        this.mediumService = mediumService;
    }

    @KafkaListener(topics = "respuesta_espiritu", groupId = "grupo-mensajeria")
    public void consumirMensajeDeEspiritu(String mensajeJson) {

        System.out.println(" Recibido mensaje de Kafka (respuesta_espiritu): " + mensajeJson);

        RespuestaEspirituDTO respuesta = gson.fromJson(mensajeJson, RespuestaEspirituDTO.class);
        mediumService.reducirCordura(respuesta.mediumId(), respuesta.espirituId());

        System.out.println(" RESPUESTA DEL ESPÍRITU:");
        System.out.println(" - Espíritu: " + respuesta.espirituId());
        System.out.println(" - Medium:   " + respuesta.mediumId());
        System.out.println(" - Mensaje:  " + respuesta.respuesta());
        System.out.println(" - Hostilidad: " + respuesta.hostilidad());

    }
}