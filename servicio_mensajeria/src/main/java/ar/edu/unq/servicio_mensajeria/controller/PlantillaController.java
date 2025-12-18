package ar.edu.unq.servicio_mensajeria.controller;

import ar.edu.unq.servicio_mensajeria.service.respuesta.ServicioDeRespuestas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plantillas")
public class PlantillaController {

    private final ServicioDeRespuestas servicioDeRespuestas;


    public PlantillaController(ServicioDeRespuestas servicioDeRespuestas) {
        this.servicioDeRespuestas = servicioDeRespuestas;
    }
    // Se pueden usar los placeholders(queda extensible para hacer mas):
    // - '{INICIAL}' para referirse a la inicial del espiritu candidato.
    // - '{LONGITUD}' para referirse a la longitud del nombre del espiritu candidato.
    @PostMapping
    public ResponseEntity<String> agregarPlantilla(@RequestBody PlantillaDTO plantilla) {
        servicioDeRespuestas.agregarPlantilla(plantilla.texto());
        return ResponseEntity.status(HttpStatus.CREATED).body("Plantilla agregada correctamente");
    }
}
