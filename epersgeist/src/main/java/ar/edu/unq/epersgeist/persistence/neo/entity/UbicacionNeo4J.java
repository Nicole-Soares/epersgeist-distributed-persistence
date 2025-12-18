package ar.edu.unq.epersgeist.persistence.neo.entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Data
@Node(primaryLabel = "Ubicacion")
public class UbicacionNeo4J {

    @Id
    private Long id;

    @Relationship(type = "CONECTA_A", direction = Relationship.Direction.OUTGOING)
    private Set<ConexionNeo> conexiones = new HashSet<>();

    public UbicacionNeo4J (Long ubicacionId){
        this.id = ubicacionId;
    }

}
