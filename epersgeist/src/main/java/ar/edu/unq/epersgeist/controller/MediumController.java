package ar.edu.unq.epersgeist.controller;
import ar.edu.unq.commons.dto.MensajeMediumDTO;
import ar.edu.unq.commons.dto.identificacionDTOs.SolicitudIdentificacionDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritu.RecuperarEspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.medium.ActualizarMediumDTO;
import ar.edu.unq.epersgeist.controller.dto.medium.CrearMediumDTO;
import ar.edu.unq.epersgeist.controller.dto.medium.EnviarMensajeMediumDTO;
import ar.edu.unq.epersgeist.controller.dto.medium.RecuperarMediumDTO;
import ar.edu.unq.epersgeist.kafka.producer.MensajeMediumProducer;
import ar.edu.unq.epersgeist.kafka.producer.SolicitudIdentificacionProducer;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.service.interfaces.MediumService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/medium")
public class MediumController {

    private final MediumService mediumService;
    private final MensajeMediumProducer producer;
    private final SolicitudIdentificacionProducer solicitudIdentificacionProducer;

    public MediumController (MediumService mediumService, MensajeMediumProducer producer, SolicitudIdentificacionProducer solicitudIdentificacionProducer) {
        this.mediumService = mediumService;
        this.producer = producer;
        this.solicitudIdentificacionProducer = solicitudIdentificacionProducer;
    }

    @PostMapping
    public ResponseEntity<RecuperarMediumDTO> crearMedium(@RequestBody @Valid CrearMediumDTO mediumDTO) {
        RecuperarMediumDTO respuestaDTO = RecuperarMediumDTO.desdeModelo(this.mediumService.create(mediumDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(respuestaDTO);
    }


    @GetMapping
    public ResponseEntity<List<RecuperarMediumDTO>> recuperarTodosLosMedium() {
        return ResponseEntity.ok(
                mediumService
                        .findAll()
                        .stream()
                        .map(RecuperarMediumDTO::desdeModelo)
                        .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecuperarMediumDTO> recuperarMedium(@PathVariable Long id) {
        RecuperarMediumDTO respuesta = RecuperarMediumDTO.desdeModelo(this.mediumService.findById(id));
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecuperarMediumDTO> actualizarMedium(@PathVariable Long id,
                                                               @RequestBody @Valid ActualizarMediumDTO mediumDTO) {
        RecuperarMediumDTO responseDTO = RecuperarMediumDTO.desdeModelo(this.mediumService.actualizarMedium(id, mediumDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{mediumExorcistaId}/exorcizar/{mediumPoseidoId}")
    public ResponseEntity<RecuperarMediumDTO> exorcizar(@PathVariable Long mediumExorcistaId,
                                                        @PathVariable Long mediumPoseidoId) {
        mediumService.exorcizar(mediumExorcistaId, mediumPoseidoId);
        RecuperarMediumDTO responseDTO = RecuperarMediumDTO.desdeModelo(mediumService.findById(mediumExorcistaId));
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/descansar/{id}")
    public ResponseEntity<RecuperarMediumDTO> descansar(@PathVariable Long id) {
        mediumService.descansar(id);
        RecuperarMediumDTO mediumRecuperado = RecuperarMediumDTO.desdeModelo(mediumService.findById(id));
        return ResponseEntity.ok(mediumRecuperado);
    }

    @PatchMapping("/{mediumId}/invocar/{espirituId}")
    public ResponseEntity<RecuperarEspirituDTO> invocar(@PathVariable Long mediumId,
                                                        @PathVariable Long espirituId) {
        RecuperarEspirituDTO espirituRecuperado = RecuperarEspirituDTO.desdeModelo(mediumService.invocar(mediumId, espirituId));
        return ResponseEntity.ok(espirituRecuperado);
    }

    @PatchMapping("/{mediumId}/mover/{latitud}/{longitud}")
    public ResponseEntity<RecuperarMediumDTO> mover(@PathVariable Long mediumId,
                                                    @PathVariable Double latitud,
                                                    @PathVariable Double longitud) {
        mediumService.mover(mediumId, latitud, longitud);
        RecuperarMediumDTO mediumRecuperado = RecuperarMediumDTO.desdeModelo(mediumService.findById(mediumId));
        return ResponseEntity.ok(mediumRecuperado);
    }

    @PostMapping("/{mediumId}/mensaje")
    public ResponseEntity<String> enviarMensaje(
            @PathVariable Long mediumId,
            @RequestBody @Valid EnviarMensajeMediumDTO request
    ) {
        Medium medium = mediumService.findById(mediumId);
        Long ubicacionId = medium.getUbicacion().getId();

        MensajeMediumDTO dto = new MensajeMediumDTO(
                mediumId,
                ubicacionId,
                request.mensaje()
        );
        //no se contacta con el service porque no hay un cambio en el modelo
        producer.enviarMensaje(dto);

        return ResponseEntity.ok("Mensaje enviado.");
    }

    /**
     * Envía la solicitud de identificación de un medium sobre un espíritu.
     * @param mediumId El id del medium que realiza la solicitud.
     * @param conjetura La conjetura del medium sobre el espíritu. Incluye nombre.
     * @return
     */
    @PostMapping("/{mediumId}/identificar")
    public ResponseEntity<String> identificarEspiritu(
            @PathVariable Long mediumId,
            @RequestBody SolicitudIdentificacionDTO.MediumConjetura conjetura
    ) {
        solicitudIdentificacionProducer.enviarSolicitud(mediumId, conjetura);

        return ResponseEntity.ok("Solicitud de identificación enviada.");
    }
}
