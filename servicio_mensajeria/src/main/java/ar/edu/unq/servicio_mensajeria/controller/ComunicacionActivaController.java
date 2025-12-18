package ar.edu.unq.servicio_mensajeria.controller;

import ar.edu.unq.servicio_mensajeria.neo.service.ComunicacionActivaService;
import ar.edu.unq.commons.neo.entity.ComunicacionActiva;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/comunicacion-activa")
public class ComunicacionActivaController {

    private final ComunicacionActivaService comunicacionActivaService;

    public ComunicacionActivaController(ComunicacionActivaService comunicacionActivaService) {
        this.comunicacionActivaService = comunicacionActivaService;
    }

    @GetMapping
    public ResponseEntity<ComunicacionActivaDTO> obtenerComunicacionActiva(
            @RequestParam Long mediumId,
            @RequestParam Long ubicacionId
    ) {
        Optional<ComunicacionActiva> opt =
                comunicacionActivaService.obtenerComunicacionActivaSiExiste(mediumId, ubicacionId);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ComunicacionActiva ca = opt.get();
        ComunicacionActivaDTO dto = new ComunicacionActivaDTO(
                ca.getMediumId(),
                ca.getUbicacionId(),
                ca.getEspirituId()
        );

        return ResponseEntity.ok(dto);
    }
}
