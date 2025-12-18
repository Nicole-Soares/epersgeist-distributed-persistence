package ar.edu.unq.epersgeist.kafka.consumer;

import ar.edu.unq.commons.dto.TemperaturaDTO;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TemperaturaActualizadaConsumer {

    private final Gson gson = new Gson();
    private final UbicacionService ubicacionService;

    public TemperaturaActualizadaConsumer(UbicacionService ubicacionService){
        this.ubicacionService = ubicacionService;
    }

    @KafkaListener(topics = "temperatura_actualizada")
    public void consumirTemperatura(String mensajeJson) {

        System.out.println("Llego mensaje: " + mensajeJson);

        TemperaturaDTO dto = gson.fromJson(mensajeJson, TemperaturaDTO.class);

        ubicacionService.actualizarTemperaturaDelConsumer(dto);
     
        System.out.println("Temperatura actualizada a " + dto.temperatura());
    }
}
