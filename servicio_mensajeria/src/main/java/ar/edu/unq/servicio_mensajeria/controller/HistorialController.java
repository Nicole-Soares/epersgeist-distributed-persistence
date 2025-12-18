package ar.edu.unq.servicio_mensajeria.controller;

import ar.edu.unq.servicio_mensajeria.mongo.entity.HistorialSesion;
import ar.edu.unq.servicio_mensajeria.mongo.repository.HistorialSesionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historial")
public class HistorialController {

    private final HistorialSesionRepository historialRepo;

    public HistorialController(HistorialSesionRepository historialRepo) {
        this.historialRepo = historialRepo;
    }

    @GetMapping("/{mediumId}")
    public List<HistorialSesion> historialPorMedium(@PathVariable Long mediumId) {
        return historialRepo.findByMediumIdOrderByFechaAsc(mediumId);
    }
}