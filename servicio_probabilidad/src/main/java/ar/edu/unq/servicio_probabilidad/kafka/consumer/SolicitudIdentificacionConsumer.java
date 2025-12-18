package ar.edu.unq.servicio_probabilidad.kafka.consumer;

import ar.edu.unq.commons.dto.identificacionDTOs.SolicitudIdentificacionDTO;
import ar.edu.unq.commons.dto.identificacionDTOs.ResultadoIdentificacionDTO;
import ar.edu.unq.servicio_probabilidad.kafka.producer.ResultadoIdentificacionProducer;
import ar.edu.unq.servicio_probabilidad.service.IdentificacionService;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SolicitudIdentificacionConsumer {

    private final Gson gson = new Gson();
    private final IdentificacionService identificacionService;
    private final ResultadoIdentificacionProducer resultadoIdentificacionProducer;


    public SolicitudIdentificacionConsumer(
            IdentificacionService identificacionService,
            ResultadoIdentificacionProducer resultadoIdentificacionProducer
    ) {
        this.identificacionService = identificacionService;
        this.resultadoIdentificacionProducer = resultadoIdentificacionProducer;
    }


    @KafkaListener(
            topics = "solicitud_identificacion",
            groupId = "grupo-probabilidad"
    )
    public void consumirSolicitud(String solicitudJson) {
        System.out.println("[servicio_probabilidad] Llegó solicitud_identificacion: " + solicitudJson);

        SolicitudIdentificacionDTO dto = gson.fromJson(solicitudJson, SolicitudIdentificacionDTO.class);

        ResultadoIdentificacionDTO resultado = identificacionService.resolverIdentificacion(dto);

        resultadoIdentificacionProducer.enviarResultado(resultado);
    }

}