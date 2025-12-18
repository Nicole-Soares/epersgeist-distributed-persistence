package ar.edu.unq.servicio_mensajeria.service.respuesta;

import ar.edu.unq.commons.neo.entity.ComunicacionActiva;
import ar.edu.unq.servicio_mensajeria.sql.entity.EspirituCandidato;

public interface ServicioDeRespuestas {

    String construirTextoConEspiritu(EspirituCandidato espirituCandidato, ComunicacionActiva comunicacion);

    void agregarPlantilla(String texto);
}
