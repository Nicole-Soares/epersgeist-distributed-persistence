package ar.edu.unq.epersgeist.persistence.repository.interfaces;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

public interface CalculadorDeDistanciaRepository {
    boolean estaDentroDelRango(GeoJsonPoint origen, GeoJsonPoint destino, double distanciaMaximaKm);
}
