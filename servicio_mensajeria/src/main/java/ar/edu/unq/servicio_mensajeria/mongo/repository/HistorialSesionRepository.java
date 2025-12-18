package ar.edu.unq.servicio_mensajeria.mongo.repository;

import ar.edu.unq.servicio_mensajeria.mongo.entity.HistorialSesion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialSesionRepository extends MongoRepository<HistorialSesion, String> {
    List<HistorialSesion> findByMediumIdOrderByFechaAsc(Long mediumId);
}
