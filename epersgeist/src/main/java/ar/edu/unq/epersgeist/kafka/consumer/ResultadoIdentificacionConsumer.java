package ar.edu.unq.epersgeist.kafka.consumer;

import ar.edu.unq.commons.dto.identificacionDTOs.ResultadoIdentificacionDTO;
import ar.edu.unq.epersgeist.service.interfaces.ResultadoIdentificacionService;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ResultadoIdentificacionConsumer {

    private final Gson gson = new Gson();
    private final ResultadoIdentificacionService resultadoIdentificacionService;

    public ResultadoIdentificacionConsumer(ResultadoIdentificacionService resultadoIdentificaconService) {
        this.resultadoIdentificacionService = resultadoIdentificaconService;
    }

    @KafkaListener(topics = "resultado_identificacion", groupId = "mi-grupo")
    public void consumirMensaje(String mensajeJson) {
        System.out.println("Recibido mensaje (resultado_identificacion): " + mensajeJson);

        ResultadoIdentificacionDTO dto = gson.fromJson(mensajeJson, ResultadoIdentificacionDTO.class);

        System.out.println(" RESULTADO DE IDENTIFICACIÓN:");
        System.out.println(" - Espíritu: " + dto.espirituId());
        System.out.println(" - Medium:   " + dto.mediumId());
        System.out.println(" - Éxito:    " + dto.exito());
        System.out.println(" - Muere:    " + dto.muere());
        System.out.println(" - Mensaje:  " + dto.mensaje());

        resultadoIdentificacionService.procesarResultado(dto);
    }

}