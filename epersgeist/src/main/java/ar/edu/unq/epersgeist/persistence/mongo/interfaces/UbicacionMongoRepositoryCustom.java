package ar.edu.unq.epersgeist.persistence.mongo.interfaces;

import ar.edu.unq.epersgeist.modelo.estadistica.ReportePromedio;
import ar.edu.unq.epersgeist.modelo.sensor.DatoSensorNormalizado;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CoordenadaMongo;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;


public interface UbicacionMongoRepositoryCustom {
    void eliminarCoordenadaEntidad(String ubicacionMongoId, Long entidadSQLId, String tipoEntidad);
    CoordenadaMongo findCoordenadaDeEntidad(Long ubicacionId, Long entidadId);
    void actualizarOcrearCoordenadaEntidad(String ubicacionMongoId, Long entidadSQLId, String tipoEntidad, Coordenadas coordenadasEntidad);
    void normalizacionDeDocumento();
    List<DatoSensorNormalizado> findAllNormalizados();
    List<ReportePromedio> obtenerPromedioPorTipoSensor();
    double distanciaEntre(GeoJsonPoint pointMedium, GeoJsonPoint pointEspiritu);
}
