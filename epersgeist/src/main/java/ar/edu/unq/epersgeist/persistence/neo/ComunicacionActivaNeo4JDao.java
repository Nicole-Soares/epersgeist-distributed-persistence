package ar.edu.unq.epersgeist.persistence.neo;

import ar.edu.unq.commons.neo.entity.ComunicacionActiva;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComunicacionActivaNeo4JDao extends Neo4jRepository<ComunicacionActiva, String> {

    @Query("MATCH (c:ComunicacionActiva) WHERE c.mediumId = $mediumId RETURN c.espirituId AS espirituId")
    Long findEspirituIdByMediumId(Long mediumId);

}
