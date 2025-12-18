package ar.edu.unq.epersgeist.modelo.sensor;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public record DatoSensorNormalizado(
        @Id
        String id,

        @Field("id_sensor")
        String idSensor,

        String tipo,
        double valor,
        String unidad,

        @Field("valor_original")
        double valorOriginal,

        @Field("unidad_original")
        String unidadOriginal,

        Instant fecha
) {
}
