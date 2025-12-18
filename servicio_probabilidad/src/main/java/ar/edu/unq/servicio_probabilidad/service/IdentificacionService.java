package ar.edu.unq.servicio_probabilidad.service;

import ar.edu.unq.commons.dto.identificacionDTOs.ResultadoIdentificacionDTO;
import ar.edu.unq.commons.dto.identificacionDTOs.SolicitudIdentificacionDTO;

public interface IdentificacionService {
    ResultadoIdentificacionDTO resolverIdentificacion(SolicitudIdentificacionDTO dto);
}
