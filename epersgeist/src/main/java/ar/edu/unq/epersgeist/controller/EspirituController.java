package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.espiritu.ActualizarEspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritu.CrearEspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritu.RecuperarEspirituDTO;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.service.interfaces.EspirituService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/espiritu")
@Validated
public class EspirituController {

    private final EspirituService espirituService;

    public EspirituController(EspirituService espirituService) {
        this.espirituService = espirituService;
    }

    @PostMapping
    public ResponseEntity<RecuperarEspirituDTO> crearEspiritu(@RequestBody @Valid CrearEspirituDTO espirituDTO) {
        RecuperarEspirituDTO responseDTO = RecuperarEspirituDTO.desdeModelo(this.espirituService.create(espirituDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecuperarEspirituDTO> recuperarEspiritu(@PathVariable("id") Long id) {
        RecuperarEspirituDTO responseDTO = RecuperarEspirituDTO.desdeModelo(this.espirituService.findById(id));
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecuperarEspirituDTO> actualizarEspiritu(@PathVariable("id") Long id,
                                                                   @RequestBody @Valid ActualizarEspirituDTO espirituDTO) {
        Espiritu espirituActualizado = espirituService.actualizarEspiritu(id, espirituDTO);
        RecuperarEspirituDTO responseDTO = RecuperarEspirituDTO.desdeModelo(espirituActualizado);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<RecuperarEspirituDTO>> recuperarTodosLosEspiritus() {
        return ResponseEntity.status(HttpStatus.OK).body(
                espirituService
                        .recuperarTodos()
                        .stream()
                        .map(RecuperarEspirituDTO::desdeModelo)
                        .toList());
    }

    @GetMapping("/demonios")
    public ResponseEntity<Page<RecuperarEspirituDTO>> recuperarDemonios(@RequestParam Sort.Direction direccion,
                                                                        @RequestParam @jakarta.validation.constraints.PositiveOrZero Integer pagina,
                                                                        @RequestParam @jakarta.validation.constraints.Positive Integer cantidadPorPagina) {
        Pageable pageable = PageRequest.of(
                pagina,
                cantidadPorPagina,
                direccion,
                "nivelDeConexion"
        );

        Page<RecuperarEspirituDTO> dtoPage = espirituService.espiritusDemoniacos(pageable).map(RecuperarEspirituDTO::desdeModelo);
        return ResponseEntity.ok(dtoPage);
    }

    @PatchMapping("/{id}/conectar/{mediumId}")
    public ResponseEntity<RecuperarEspirituDTO> conectar(@PathVariable Long id,
                                                         @PathVariable Long mediumId) {
        espirituService.conectar(id, mediumId);
        RecuperarEspirituDTO responseDTO = RecuperarEspirituDTO.desdeModelo(espirituService.findById(id));
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{mediumId}/espiritus")
    public ResponseEntity<List<RecuperarEspirituDTO>> espiritusConectadosA(@PathVariable Long mediumId) {
        List<RecuperarEspirituDTO> respondeDTO = espirituService.espiritusDelMedium(mediumId)
                .stream()
                .map(RecuperarEspirituDTO::desdeModelo)
                .toList();
        return ResponseEntity.ok(respondeDTO);
    }

    @PatchMapping("/{espirituADominarId}/dominar/{espirituDominanteId}")
    public ResponseEntity<RecuperarEspirituDTO> dominar(@PathVariable Long espirituADominarId,
                                                        @PathVariable Long espirituDominanteId) {
        espirituService.dominar(espirituDominanteId, espirituADominarId);
        RecuperarEspirituDTO responseDTO = RecuperarEspirituDTO.desdeModelo(espirituService.findById(espirituADominarId));
        return ResponseEntity.ok(responseDTO);
    }
}