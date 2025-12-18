package ar.edu.unq.epersgeist.persistence.repository.impl;

import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.CoordenadasMapper;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CoordenadaMongo;
import ar.edu.unq.epersgeist.persistence.mongo.entity.EntidadEnCoordenadaMongo;
import ar.edu.unq.epersgeist.persistence.mongo.entity.UbicacionMongo;
import ar.edu.unq.epersgeist.persistence.mongo.interfaces.UbicacionMongoDAO;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.CoordenadaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CoordenadasRepositoryImpl implements CoordenadaRepository {

    private final UbicacionMongoDAO ubicacionMongoDAO;
    private final CoordenadasMapper coordenadasMapper;

    public CoordenadasRepositoryImpl(UbicacionMongoDAO ubicacionMongoDAO, CoordenadasMapper coordenadasMapper) {
        this.ubicacionMongoDAO = ubicacionMongoDAO;
        this.coordenadasMapper = coordenadasMapper;
    }

    //Helper para no repetir codigo
    private UbicacionMongo obtenerUbicacion(Long ubicacionSQLId) {
        return ubicacionMongoDAO.findById(ubicacionSQLId.toString()).orElseThrow(
                () -> new EntityNotFoundException("No se encontró la ubicación con ID: " + ubicacionSQLId)
        );
    }

    /**
     * Guarda la coordenada dentro del documento UbicacionMongo correspondiente
     * y registra al Medium (con su ID) dentro de esa coordenada.
     */
    @Override
    public Coordenadas save(Long entidadSQLId, Long ubicacionSQLId, Coordenadas coordenadas, String tipoEntidad) {

        // ubicacion mongo
        UbicacionMongo ubicacionMongo = this.obtenerUbicacion(ubicacionSQLId);

        if (ubicacionMongo.getCoordenadas() == null) {
            ubicacionMongo.setCoordenadas(new ArrayList<>());
        }

        // Buscar o crear la coordenada
        CoordenadaMongo coordenadaMongo = ubicacionMongo.getCoordenadas().stream()
                .filter(c -> c.getLat() == coordenadas.getLatitud() && c.getLon() == coordenadas.getLongitud())
                .findFirst()
                .orElseGet(() -> {
                    CoordenadaMongo nueva = coordenadasMapper.aEntidad(coordenadas);
                    ubicacionMongo.getCoordenadas().add(nueva);
                    return nueva;
                });

        // Agregar la entidad dentro de entidadesEnCoors si es que no existe ya
        List<EntidadEnCoordenadaMongo> entidades = coordenadaMongo.getEntidadesEnCoors();

        if (!yaExisteLaEntidad(entidades, entidadSQLId, tipoEntidad) && entidadSQLId != null) {
            entidades.add(new EntidadEnCoordenadaMongo(entidadSQLId, tipoEntidad));
        }


        ubicacionMongoDAO.save(ubicacionMongo);

        return coordenadasMapper.aModelo(coordenadaMongo);
    }

    @Override
    public double calcularDistanciaEntre(GeoJsonPoint pointMedium, GeoJsonPoint pointEspiritu) {
        return ubicacionMongoDAO.distanciaEntre(pointMedium, pointEspiritu);
    }

    private boolean yaExisteLaEntidad(List<EntidadEnCoordenadaMongo> entidades, Long entidadSQLId, String tipoEntidad) {
        return  entidades.stream()
                .anyMatch(e ->
                        Objects.equals(e.getEntidadSQLId(), entidadSQLId) &&
                                Objects.equals(e.getEntidadRef(), tipoEntidad)
                );
    }
}
