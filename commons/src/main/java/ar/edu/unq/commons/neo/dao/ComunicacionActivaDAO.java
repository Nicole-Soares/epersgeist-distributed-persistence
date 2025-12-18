package ar.edu.unq.commons.neo.dao;

import ar.edu.unq.commons.neo.entity.ComunicacionActiva;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComunicacionActivaDAO extends Neo4jRepository<ComunicacionActiva, String> {

    Optional<ComunicacionActiva> findByMediumIdAndUbicacionId(@Param("mediumId") Long mediumId,
                                                              @Param("ubicacionId") Long ubicacionId);

    void deleteByMediumIdAndUbicacionId(@Param("mediumId") Long mediumId,
                                        @Param("ubicacionId") Long ubicacionId);

    @Query("""
            MERGE (c:ComunicacionActiva {mediumId: $mediumId, ubicacionId: $ubicacionId})
            SET c.espirituId = $espirituId,
                c.nivelPista = $nivelPista
            RETURN c
            """)
    void actualizarComunicacionActiva(
            @Param("mediumId") Long mediumId,
            @Param("ubicacionId") Long ubicacionId,
            @Param("espirituId") Long espirituId,
            @Param("nivelPista") int nivelPista);


    @Query("""
            MATCH (c:ComunicacionActiva {espirituId: $espirituId})
            DETACH DELETE c
            """)
    void deleteEspirituById(@Param("espirituId") Long espirituId);
}
