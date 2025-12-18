package ar.edu.unq.epersgeist.persistence.repository.interfaces;

import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

public interface CoordenadaRepository {
    Coordenadas save(Long entidadSQLId, Long ubicacionSQLId, Coordenadas coordenadas, String tipoEntidad);
    double calcularDistanciaEntre(GeoJsonPoint pointMedium, GeoJsonPoint pointEspiritu);
}
