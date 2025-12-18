package ar.edu.unq.epersgeist.persistence.mongo.interfaces;

import ar.edu.unq.epersgeist.persistence.mongo.entity.UbicacionMongo;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UbicacionMongoDAO extends MongoRepository<UbicacionMongo, String>, UbicacionMongoRepositoryCustom {

    /**
     * Verifica si existe alguna UbicacionMongo cuyo area intersecta con el poligono dado.
     * @param poligono El poligono con el cual se verifica la interseccion.
     * @return true si existe al menos una UbicacionMongo que intersecta con el
     */
    @Query(value = "{ 'area': { $geoIntersects: { $geometry: ?0 } } }", exists = true)
    boolean existsByAreaIntersecting(GeoJsonPolygon poligono);

    /**
     * Devuelve la UbicacionMongo cuya area contenga ese punto dado.
     * @param punto El punto el cual ser quiere saber a que ubicacion pertenece.
     * @return La UbicacionMongo que contenga ese punto, si no hay ninguna devuelve null
     */
    @Query("{ 'area': { $geoIntersects: { $geometry: ?0 } } }")
    UbicacionMongo findByPuntoDentroDelArea(GeoJsonPoint punto);
}