package ar.edu.unq.epersgeist.persistence.neo.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Data
@RelationshipProperties
public class ConexionNeo {
    @Id
    @GeneratedValue
    private Long id;
    private Long costo;

    @TargetNode
    private UbicacionNeo4J destino;

    public ConexionNeo(Long costo, UbicacionNeo4J destino) {
        this.costo = costo;
        this.destino = destino;
    }
}
