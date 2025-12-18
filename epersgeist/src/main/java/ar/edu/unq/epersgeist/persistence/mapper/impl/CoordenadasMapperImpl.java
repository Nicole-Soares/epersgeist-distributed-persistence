package ar.edu.unq.epersgeist.persistence.mapper.impl;

import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.EntidadCoordenadaInfo;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.CoordenadasMapper;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CoordenadaMongo;
import ar.edu.unq.epersgeist.persistence.mongo.entity.EntidadEnCoordenadaMongo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CoordenadasMapperImpl implements CoordenadasMapper {

    @Override
    public CoordenadaMongo aEntidad(Coordenadas coordenadas, List<? extends EntidadCoordenadaInfo> entidadesInfo) {
        List<EntidadEnCoordenadaMongo> entidadesEnCoors = new ArrayList<>();

        for (EntidadCoordenadaInfo entidadInfo : entidadesInfo) {
            entidadesEnCoors.add(new EntidadEnCoordenadaMongo(
                    entidadInfo.getId(),
                    entidadInfo.getRef()
            ));
        }

        return new CoordenadaMongo(
                coordenadas.getLongitud(),
                coordenadas.getLatitud(),
                entidadesEnCoors
        );
    }

    @Override
    public CoordenadaMongo aEntidad(Coordenadas coordenadas) {
        return new CoordenadaMongo(
                coordenadas.getLongitud(),
                coordenadas.getLatitud(),
                new ArrayList<>()
        );
    }

    public Coordenadas aModelo(CoordenadaMongo coordenadaMongo) {
        double lon = Math.floor(coordenadaMongo.getLon() * 1000) / 1000.0;
        double lat = Math.floor(coordenadaMongo.getLat() * 1000) / 1000.0;
        return new Coordenadas(lon, lat);
    }
}
