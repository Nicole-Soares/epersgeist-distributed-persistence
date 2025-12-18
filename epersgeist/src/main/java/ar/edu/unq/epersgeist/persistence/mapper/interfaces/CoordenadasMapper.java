package ar.edu.unq.epersgeist.persistence.mapper.interfaces;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.EntidadCoordenadaInfo;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CoordenadaMongo;

import java.util.List;

public interface CoordenadasMapper extends EntityMapper<Coordenadas, CoordenadaMongo>{
    CoordenadaMongo aEntidad(Coordenadas coordenadas, List<? extends EntidadCoordenadaInfo> entidadesInfo);
}
