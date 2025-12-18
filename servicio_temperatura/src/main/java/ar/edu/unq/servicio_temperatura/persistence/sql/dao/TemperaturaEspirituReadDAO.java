package ar.edu.unq.servicio_temperatura.persistence.sql.dao;

import ar.edu.unq.servicio_temperatura.persistence.sql.entities.EspirituTempSQL;
import ar.edu.unq.servicio_temperatura.persistence.sql.projection.HostilidadPorUbicacionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemperaturaEspirituReadDAO extends JpaRepository<EspirituTempSQL, Long> {

    @Query(value = """
    SELECT e.ubicacion_id AS ubicacionId,
           AVG(e.hostilidad) AS hostilidadTotal
    FROM espiritu e
    WHERE e.deleted_at = false
    GROUP BY e.ubicacion_id
    """,
            nativeQuery = true)
    List<HostilidadPorUbicacionProjection> getHostilidadAgrupada();
}