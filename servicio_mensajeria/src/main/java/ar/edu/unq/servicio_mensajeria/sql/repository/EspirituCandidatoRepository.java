package ar.edu.unq.servicio_mensajeria.sql.repository;

import ar.edu.unq.servicio_mensajeria.sql.entity.EspirituCandidato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EspirituCandidatoRepository extends JpaRepository<EspirituCandidato, Long> {

    @Query(value = """
            SELECT *
            FROM espiritu 
            WHERE ubicacion_id = :ubicacionId
                AND deleted_at = false 
            ORDER BY random()
            LIMIT 1
            """,
        nativeQuery = true
    )
    Optional<EspirituCandidato> buscarCandidatoAleatorio(@Param("ubicacionId") Long ubicacionId);

    // esta es para cuando ya tengo el id del espiritu de la comunicacion
    Optional<EspirituCandidato> findByIdAndDeletedAtFalse(Long id);
}
