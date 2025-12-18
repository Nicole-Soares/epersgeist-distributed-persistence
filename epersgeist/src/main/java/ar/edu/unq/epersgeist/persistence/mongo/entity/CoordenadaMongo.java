package ar.edu.unq.epersgeist.persistence.mongo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CoordenadaMongo {

    private GeoJsonPoint punto;
    private List<EntidadEnCoordenadaMongo> entidadesEnCoors;

    public CoordenadaMongo(double lon, double lat, List<EntidadEnCoordenadaMongo> entidadesEnCoors) {
        this.punto = new GeoJsonPoint(lon, lat);
        this.entidadesEnCoors = entidadesEnCoors;
    }

    public double getLat() {
        return punto.getY();
    }

    public double getLon() {
        return punto.getX();
    }

}