package ar.edu.unq.epersgeist.controller.dto.sensor;

import ar.edu.unq.epersgeist.modelo.sensor.DatoSensorNormalizado;

import java.time.Instant;

public record DatoSensorNormalizadoDTO(String idSensor, String tipo, double valor, String unidad,
                                       double valorOriginal, String unidadOriginal, Instant fecha) {

    public static DatoSensorNormalizadoDTO desdeModelo(DatoSensorNormalizado datoSensorNormalizado) {
        return new DatoSensorNormalizadoDTO(
                datoSensorNormalizado.idSensor(),
                datoSensorNormalizado.tipo(),
                datoSensorNormalizado.valor(),
                datoSensorNormalizado.unidad(),
                datoSensorNormalizado.valorOriginal(),
                datoSensorNormalizado.unidadOriginal(),
                datoSensorNormalizado.fecha());
    }
}
