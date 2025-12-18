package ar.edu.unq.epersgeist.persistence.mapper.interfaces;

import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.EntidadCoordenadaInfo;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistence.mongo.entity.UbicacionMongo;
import ar.edu.unq.epersgeist.persistence.neo.entity.UbicacionNeo4J;
import ar.edu.unq.epersgeist.persistence.sql.entity.UbicacionSQL;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import java.util.List;
import java.util.Set;

public interface UbicacionMapper extends DualEntityMapper<Ubicacion, UbicacionSQL, UbicacionMongo> {

    UbicacionMongo aEntidadMongoConEntidades(Ubicacion ubicacion, List<Coordenadas> coordenadas, List<List<EntidadCoordenadaInfo>> entidadesPorCoordenada);

    Ubicacion aModelo(UbicacionSQL ubicacionSQL, UbicacionNeo4J ubicacionNeo4J, UbicacionMongo ubicacionMongo);

    UbicacionNeo4J aEntidadNeo4J(Ubicacion ubicacion);

    UbicacionMongo aEntidadMongo(Ubicacion ubicacion, List<Coordenadas> coordenadas);

    GeoJsonPolygon aPoligono(Set<Coordenadas> vertices);
}
