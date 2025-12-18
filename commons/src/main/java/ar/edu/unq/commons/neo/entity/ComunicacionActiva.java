package ar.edu.unq.commons.neo.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Getter
@Node("ComunicacionActiva")
@Setter
public class ComunicacionActiva {

    @Id @GeneratedValue
    private String id;
    private Long mediumId;
    private Long ubicacionId;
    private Long espirituId;
    private int nivelPista = 0;

    public ComunicacionActiva() {
    }
    
    public ComunicacionActiva(Long mediumId, Long ubicacionId, Long espirituId) {
        this.mediumId = mediumId;
        this.ubicacionId = ubicacionId;
        this.espirituId = espirituId;
    }
}
