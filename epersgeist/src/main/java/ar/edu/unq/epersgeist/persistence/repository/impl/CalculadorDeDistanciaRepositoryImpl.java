package ar.edu.unq.epersgeist.persistence.repository.impl;

import ar.edu.unq.epersgeist.persistence.mongo.entity.CalculadorDeDistancia;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.CalculadorDeDistanciaRepository;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Component;

@Component
public class CalculadorDeDistanciaRepositoryImpl implements CalculadorDeDistanciaRepository {

    private MongoTemplate mongoTemplate;

    public CalculadorDeDistanciaRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean estaDentroDelRango(GeoJsonPoint origen, GeoJsonPoint destino, double distanciaMaximaKm) {
        // Limpiar la colección auxiliar
        mongoTemplate.remove(new org.springframework.data.mongodb.core.query.Query(), CalculadorDeDistancia.class);

        // Insertar ambos puntos
        mongoTemplate.insert(new CalculadorDeDistancia(origen));
        mongoTemplate.insert(new CalculadorDeDistancia(destino));

        // Ejecutar $geoNear desde el punto origen
        NearQuery nearQuery = NearQuery.near(origen, Metrics.KILOMETERS)
                .maxDistance(distanciaMaximaKm)
                .spherical(true);

        // Buscar si el destino está dentro del rango
        return mongoTemplate.geoNear(nearQuery, CalculadorDeDistancia.class)
                .getContent()
                .stream()
                .anyMatch(result -> result.getContent().getPunto().equals(destino));
    }


}
