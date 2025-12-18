package ar.edu.unq.servicio_probabilidad.service.impl;

import ar.edu.unq.commons.dto.identificacionDTOs.ResultadoIdentificacionDTO;
import ar.edu.unq.commons.dto.identificacionDTOs.SolicitudIdentificacionDTO;
import ar.edu.unq.servicio_probabilidad.modelo.espiritu.Espiritu;
import ar.edu.unq.servicio_probabilidad.modelo.identificacion.Identificacion;
import ar.edu.unq.servicio_probabilidad.modelo.identificacion.ProbabilidadIdentificacion;
import ar.edu.unq.servicio_probabilidad.modelo.identificacion.ResultadoIdentificacion;
import ar.edu.unq.servicio_probabilidad.modelo.medium.ConjeturaMedium;
import ar.edu.unq.servicio_probabilidad.service.IdentificacionService;
import org.springframework.stereotype.Service;

@Service
public class IdentificacionServiceImpl implements IdentificacionService {
    @Override
    public ResultadoIdentificacionDTO resolverIdentificacion(SolicitudIdentificacionDTO dto) {

        Espiritu espiritu = new Espiritu(
                dto.real().espirituId(),
                dto.real().nombre(),
                dto.real().tipo(),
                dto.real().hostilidadNormalizada()
        );

        ConjeturaMedium conjetura = new ConjeturaMedium(
                dto.mediumConjetura().nombre(),
                "Fuera de servicio"
        );

        Identificacion identificacion = new Identificacion(
                espiritu,
                conjetura,
                dto.corduraNormalizada(),
                new ProbabilidadIdentificacion()
        );

        ResultadoIdentificacion res = identificacion.resolver();

        return new ResultadoIdentificacionDTO(
                res.isExito(),
                res.isMuere(),
                res.getMensaje(),
                res.getEspirituId(),
                dto.mediumId()
        );
    }
}
