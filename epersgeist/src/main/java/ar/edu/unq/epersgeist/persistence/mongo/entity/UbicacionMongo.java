package ar.edu.unq.epersgeist.persistence.mongo.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import java.util.List;

@NoArgsConstructor
@Data
@Document(collection = "ubicaciones")
public class UbicacionMongo {

    @Id
    private String id;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPolygon area;

    private List<CoordenadaMongo> coordenadas;

    public UbicacionMongo(Long ubicacionId, GeoJsonPolygon area, List<CoordenadaMongo> coordenadas) {
        this.id = ubicacionId.toString();
        this.area = area;
        this.coordenadas = coordenadas;
    }

}