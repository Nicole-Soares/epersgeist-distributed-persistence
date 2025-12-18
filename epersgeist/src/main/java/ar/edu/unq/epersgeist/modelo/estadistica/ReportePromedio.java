package ar.edu.unq.epersgeist.modelo.estadistica;

import org.springframework.data.mongodb.core.mapping.Field;

public record ReportePromedio(
        @Field("_id")
        String tipoSensor,

        @Field("promedio")
        double valorPromedio,

        @Field("unidad")
        String unidadFinal
) {
}
