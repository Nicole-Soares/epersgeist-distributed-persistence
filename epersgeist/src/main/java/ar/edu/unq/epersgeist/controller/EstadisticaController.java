package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.estadistica.ReportePromedioDTO;
import ar.edu.unq.epersgeist.controller.dto.estadistica.ReporteSantuarioMasCorruptoDTO;
import ar.edu.unq.epersgeist.service.interfaces.EstadisticaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/estadistica")
public class EstadisticaController {

    EstadisticaService estadisticaService;

    public EstadisticaController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    @GetMapping("/santuarioCorrupto")
    public ResponseEntity<ReporteSantuarioMasCorruptoDTO> estadisticaSantuarios() {
        ReporteSantuarioMasCorruptoDTO responseDTO = ReporteSantuarioMasCorruptoDTO.desdeModelo(estadisticaService.santuarioCorrupto());
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/promedioSensor")
    public ResponseEntity<List<ReportePromedioDTO>> obtenerPromedioPorTipoSensor() {
        return ResponseEntity.status(HttpStatus.OK).body(
                estadisticaService.obtenerPromedioPorTipoSensor().stream()
                .map(ReportePromedioDTO::desdeModelo)
                .toList());
    }
}
