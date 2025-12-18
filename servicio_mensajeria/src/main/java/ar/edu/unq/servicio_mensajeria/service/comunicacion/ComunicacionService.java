package ar.edu.unq.servicio_mensajeria.service.comunicacion;

import ar.edu.unq.commons.dto.MensajeMediumDTO;
import ar.edu.unq.commons.dto.RespuestaEspirituDTO;

import java.util.Optional;

public interface ComunicacionService {
    MensajeMediumDTO getMensajeMediumDTO(String mensajeJson);

    Optional<RespuestaEspirituDTO> procesarMensajeMedium(MensajeMediumDTO dto);
}
