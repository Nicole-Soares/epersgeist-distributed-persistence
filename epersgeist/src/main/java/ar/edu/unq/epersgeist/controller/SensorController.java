package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.sensor.DatoSensorNormalizadoDTO;
import ar.edu.unq.epersgeist.service.interfaces.SensorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/sensor")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService){
        this.sensorService = sensorService;
    }

    @PostMapping("/normalizarDatos")
    public ResponseEntity<List<DatoSensorNormalizadoDTO>> normalizarYObtenerDatos() {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorService.ejecutarNormalizacionYConsultar().stream()
                .map(DatoSensorNormalizadoDTO::desdeModelo)
                .toList());
    }
}
