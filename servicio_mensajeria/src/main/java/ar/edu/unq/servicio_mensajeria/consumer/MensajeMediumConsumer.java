package ar.edu.unq.servicio_mensajeria.consumer;

import ar.edu.unq.commons.dto.MensajeMediumDTO;
import ar.edu.unq.commons.dto.RespuestaEspirituDTO;
import ar.edu.unq.servicio_mensajeria.producer.RespuestaEspirituProducer;
import ar.edu.unq.servicio_mensajeria.producer.SinEspiritusProducer;
import ar.edu.unq.servicio_mensajeria.service.comunicacion.ComunicacionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MensajeMediumConsumer {
    private final ComunicacionService comunicacionService;
    private final RespuestaEspirituProducer respuestaEspirituProducer;
    private final SinEspiritusProducer sinEspiritusProducer;

    public MensajeMediumConsumer(ComunicacionService comunicacionService, RespuestaEspirituProducer respuestaEspirituProducer, SinEspiritusProducer sinEspiritusProducer) {
        this.comunicacionService = comunicacionService;
        this.respuestaEspirituProducer = respuestaEspirituProducer;
        this.sinEspiritusProducer = sinEspiritusProducer;
    }

    @KafkaListener(topics = "mensaje_medium", groupId = "grupo-mensajeria")
    public void consumirMensaje(String mensajeJson) {
        MensajeMediumDTO dto = comunicacionService.getMensajeMediumDTO(mensajeJson);

        Optional<RespuestaEspirituDTO> respuestaOpcional =
                comunicacionService.procesarMensajeMedium(dto);

        respuestaOpcional.ifPresent(respuestaEspirituProducer::enviarRespuesta);
    }
}
