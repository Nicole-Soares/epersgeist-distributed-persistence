package ar.edu.unq.epersgeist.controller.dto.estadistica;

import ar.edu.unq.epersgeist.modelo.estadistica.ReportePromedio;

public record ReportePromedioDTO(String tipoSensor, String valorPromedio, String unidadFinal) {

    public static ReportePromedioDTO desdeModelo(ReportePromedio reporte) {
        String valorPromedioStr = String.valueOf(reporte.valorPromedio());

        return new ReportePromedioDTO(
                reporte.tipoSensor(),
                valorPromedioStr,
                reporte.unidadFinal()
        );
    }
}