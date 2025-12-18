package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.espiritu.RecuperarEspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.medium.RecuperarMediumDTO;
import ar.edu.unq.epersgeist.controller.dto.ubicacion.CrearUbicacionDTO;
import ar.edu.unq.epersgeist.controller.dto.ubicacion.RecuperarUbicacionDTO;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/ubicacion")
public class UbicacionController {
    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @PostMapping
    public ResponseEntity<RecuperarUbicacionDTO> crearUbicacion(@RequestBody @Valid CrearUbicacionDTO ubicacionRequest) {
        RecuperarUbicacionDTO respuestaDTO = RecuperarUbicacionDTO.desdeModelo(this.ubicacionService.createDTO(ubicacionRequest));
        return ResponseEntity.status(201).body(respuestaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecuperarUbicacionDTO> recuperarUbicacion(@PathVariable Long id) {
        Ubicacion ubicacion = ubicacionService.findByIdConConexiones(id);
        RecuperarUbicacionDTO respuestaDTO = RecuperarUbicacionDTO.desdeModelo(ubicacion);
        return ResponseEntity.ok(respuestaDTO);
    }

    @GetMapping
    public ResponseEntity<List<RecuperarUbicacionDTO>> recuperarTodasLasUbicaciones() {
        List<RecuperarUbicacionDTO> ubicaciones = ubicacionService
                .recuperarTodos()
                .stream()
                .map(RecuperarUbicacionDTO::desdeModelo)
                .toList();
        return ResponseEntity.ok(ubicaciones);
    }

    @GetMapping("/{id}/espiritus")
    public ResponseEntity<List<RecuperarEspirituDTO>> espiritusEnUbicacion(@PathVariable Long id) {
        List<RecuperarEspirituDTO> espiritus = ubicacionService
                .espiritusEn(id)
                .stream()
                .map(RecuperarEspirituDTO::desdeModelo)
                .toList();
        return ResponseEntity.ok(espiritus);
    }

    @GetMapping("/{id}/mediumsSinEspiritus")
    public ResponseEntity<List<RecuperarMediumDTO>> mediumsSinEspiritusEnUbicacion(@PathVariable Long id) {
        List<RecuperarMediumDTO> mediumsSinEspiritus = ubicacionService
                .mediumsSinEspiritusEn(id)
                .stream()
                .map(RecuperarMediumDTO::desdeModelo)
                .toList();
        return ResponseEntity.ok(mediumsSinEspiritus);
    }

    @PatchMapping("/{ubicacion_origen}/conectarA/{ubicacion_destino}")
    public ResponseEntity<RecuperarUbicacionDTO> conectarUbicaciones(@PathVariable Long ubicacion_origen,
                                                                     @PathVariable Long ubicacion_destino,
                                                                     @RequestParam Long costo_conexion) {
        ubicacionService.conectar(ubicacion_origen, ubicacion_destino, costo_conexion);
        RecuperarUbicacionDTO response = RecuperarUbicacionDTO.desdeModelo(ubicacionService.findByIdConConexiones(ubicacion_origen));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{ubicacion_origen}/estaConectadaPorUnPasoCon/{ubicacion_destino}")
    public boolean estanConectadasPorUnPaso(@PathVariable Long ubicacion_origen,
                                            @PathVariable Long ubicacion_destino) {
        return ubicacionService.estanConectadas(ubicacion_origen, ubicacion_destino);
    }
  
    @GetMapping("/{idOrigen}/caminoMasRentableA/{idDestino}")
    public ResponseEntity<List<RecuperarUbicacionDTO>> caminoMasRentable(@PathVariable Long idOrigen,
                                                                         @PathVariable Long idDestino) {
        List<RecuperarUbicacionDTO> caminoMasRentable = ubicacionService
                                                            .caminoMasRentable(idOrigen, idDestino)
                                                            .stream()
                                                            .map(RecuperarUbicacionDTO::desdeModelo)
                                                            .toList();
        return ResponseEntity.ok(caminoMasRentable);
    }

    @GetMapping("/{idOrigen}/caminoMasCorto/{idDestino}")
    public ResponseEntity<List<RecuperarUbicacionDTO>> caminoMasCorto(@PathVariable Long idOrigen,
                                                                      @PathVariable Long idDestino) {
        List<RecuperarUbicacionDTO> caminoMasCorto = ubicacionService
                .caminoMasCorto(idOrigen, idDestino)
                .stream()
                .map(RecuperarUbicacionDTO::desdeModelo)
                .toList();
        return ResponseEntity.ok(caminoMasCorto);
    }

    @GetMapping("/sobrecargadas")
    public ResponseEntity<List<RecuperarUbicacionDTO>> getUbicacionesSobrecargadas(@RequestParam Integer umbral) {
        List<RecuperarUbicacionDTO> ubicaciones = ubicacionService
                                            .ubicacionesSobrecargadas(umbral)
                                            .stream()
                                            .map(RecuperarUbicacionDTO::desdeModelo)
                                            .toList();
        return ResponseEntity.ok(ubicaciones);
    }
}
