package ar.edu.unq.servicio_temperatura.consumer;

import ar.edu.unq.commons.dto.SinEspiritusEnUbicacionDTO;
import ar.edu.unq.servicio_temperatura.service.TemperaturaService;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class SinEspiritusEnUbicacionConsumer {

    private final Gson gson = new Gson();
    private final TemperaturaService service;

    public SinEspiritusEnUbicacionConsumer(TemperaturaService service) {
        this.service = service;
    }

    @KafkaListener(
            topics = "sin_espiritus_en_ubicacion",
            groupId = "grupo-temperatura"
    )
    public void consumirEvento(String mensajeJson) {

        SinEspiritusEnUbicacionDTO dto = gson.fromJson(mensajeJson, SinEspiritusEnUbicacionDTO.class);

        System.out.println("[temperatura] LLego sin_espiritus_en_ubicacion: " + mensajeJson);

        service.enviarTemperaturaSinEspiritus(dto.ubicacionId());
    }
}
