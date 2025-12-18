package ar.edu.unq.epersgeist.persistence.mongo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.Id;

import java.util.Date;

@Data
@Document(collection = "normalized")
public class SensorNormalizado {

    @Id
    private String id;
    private String sensor_id;
    private String tipo;
    private Double valor;
    private String unidad;
    private Date fecha;
    private String ubicacion;
}
