package ar.edu.unq.epersgeist.persistence.mongo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EntidadEnCoordenadaMongo {

    private Long entidadSQLId;
    private String entidadRef;

    public EntidadEnCoordenadaMongo(Long entidadSQLId, String entidadRef) {
        this.entidadSQLId = entidadSQLId;
        this.entidadRef = entidadRef;
    }
}