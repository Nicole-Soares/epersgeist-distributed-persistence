package ar.edu.unq.epersgeist.persistence.mapper.impl;

import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.EntidadCoordenadaInfo;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.CoordenadasMapper;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.UbicacionMapper;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CoordenadaMongo;
import ar.edu.unq.epersgeist.persistence.mongo.entity.UbicacionMongo;
import ar.edu.unq.epersgeist.persistence.neo.entity.UbicacionNeo4J;
import ar.edu.unq.epersgeist.persistence.sql.entity.UbicacionSQL;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UbicacionMapperImpl implements UbicacionMapper {

    private final CoordenadasMapper coordenadasMapper;

    public UbicacionMapperImpl(CoordenadasMapper coordenadasMapper) {
        this.coordenadasMapper = coordenadasMapper;
    }

    @Override
    public UbicacionSQL aEntidad(Ubicacion ubicacion) {
        UbicacionSQL ubicacionSQL = new UbicacionSQL(ubicacion.getNombre(), ubicacion.getFlujoEnergia(), ubicacion.getTipo(), ubicacion.getTemperatura());
        ubicacionSQL.setId(ubicacion.getId());
        return ubicacionSQL;
    }


    private Ubicacion mapeoConexiones(Ubicacion ubicacion, UbicacionNeo4J ubicacionNeo4J) {
        ubicacionNeo4J.getConexiones().forEach(conexionNeo -> {
            Ubicacion destino = new Ubicacion();
            destino.setId(conexionNeo.getDestino().getId());
            ubicacion.getConexiones().put(destino.getId(), conexionNeo.getCosto());
        });
        return ubicacion;
    }

    @Override
    public UbicacionNeo4J aEntidadNeo4J(Ubicacion ubicacion) {
        return new UbicacionNeo4J(ubicacion.getId());
    }


    @Override
    public UbicacionMongo aEntidadSecundaria(Ubicacion ubicacion) {
        List<Point> points = ubicacion.getVertices().stream()
                .map(v -> new Point(v.getLongitud(), v.getLatitud()))
                .collect(Collectors.toList());

        points.add(points.get(0));

        GeoJsonPolygon poligonoDeUbicacion = new GeoJsonPolygon(points);
        return new UbicacionMongo(ubicacion.getId(), poligonoDeUbicacion, new ArrayList<>());
    }


    @Override
    public UbicacionMongo aEntidadMongo(Ubicacion ubicacion, List<Coordenadas> coordenadas) {
        UbicacionMongo ubicacionMongo = this.aEntidadSecundaria(ubicacion);


        List<CoordenadaMongo> coordenadasMongo = coordenadas.stream()
                .map(coordenadasMapper::aEntidad)
                .collect(Collectors.toList());

        ubicacionMongo.setCoordenadas(coordenadasMongo);

        return ubicacionMongo;
    }


    @Override
    public UbicacionMongo aEntidadMongoConEntidades(Ubicacion ubicacion, List<Coordenadas> coordenadas, List<List<EntidadCoordenadaInfo>> entidadesPorCoordenada) {
        UbicacionMongo ubicacionMongo = this.aEntidadSecundaria(ubicacion);

        List<CoordenadaMongo> coordenadasMongo = new ArrayList<>();
        for (int i = 0; i < coordenadas.size(); i++) {
            CoordenadaMongo coordMongo = coordenadasMapper.aEntidad(coordenadas.get(i), entidadesPorCoordenada.get(i));
            coordenadasMongo.add(coordMongo);
        }

        ubicacionMongo.setCoordenadas(coordenadasMongo);
        return ubicacionMongo;
    }


    @Override
    public Ubicacion aModelo(UbicacionSQL ubicacionSQL, UbicacionNeo4J ubicacionNeo4J, UbicacionMongo ubicacionMongo) {
        Ubicacion ubicacion = this.aModelo(ubicacionSQL, ubicacionMongo);
        return this.mapeoConexiones(ubicacion, ubicacionNeo4J);
    }


    @Override
    public Ubicacion aModelo(UbicacionSQL ubicacionSQL, UbicacionMongo ubicacionMongo) {
        Ubicacion ubicacion = new Ubicacion(ubicacionSQL.getNombre(), ubicacionSQL.getFlujoEnergia(), ubicacionSQL.getTipo(), verticesDesdeMongo(ubicacionMongo));
        ubicacion.setId(ubicacionSQL.getId());
        ubicacion.setTemperatura(ubicacionSQL.getTemperatura());
        return ubicacion;
    }

    private Set<Coordenadas> verticesDesdeMongo(UbicacionMongo ubicacionMongo) {
        return ubicacionMongo.getArea().getCoordinates().getFirst().getCoordinates().stream()
                .map(p -> new Coordenadas(p.getY(), p.getX()))
                .collect(Collectors.toSet());
    }

    @Override
    public GeoJsonPolygon aPoligono(Set<Coordenadas> vertices) {
        List<Point> points = vertices.stream()
                .map(v -> new Point(v.getLongitud(), v.getLatitud()))
                .collect(Collectors.toList());

        points.add(points.get(0));

        return new GeoJsonPolygon(points);
    }
}