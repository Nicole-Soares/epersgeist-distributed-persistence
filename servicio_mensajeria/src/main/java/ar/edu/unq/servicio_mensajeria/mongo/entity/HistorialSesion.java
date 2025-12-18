package ar.edu.unq.servicio_mensajeria.mongo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document("historial_sesion")
public class HistorialSesion {

    @Id
    private String id;

    private Long mediumId;
    private Long espirituId;
    private Long ubicacionId;
    private String mensajeMedium;
    private String respuestaEspiritu;
    private Instant fecha;

    public HistorialSesion(Long mediumId,
                           Long espirituId,
                           Long ubicacionId,
                           String mensajeMedium,
                           String respuestaEspiritu) {

        this.mediumId = mediumId;
        this.espirituId = espirituId;
        this.ubicacionId = ubicacionId;
        this.mensajeMedium = mensajeMedium;
        this.respuestaEspiritu = respuestaEspiritu;
        this.fecha = Instant.now();
    }
}
