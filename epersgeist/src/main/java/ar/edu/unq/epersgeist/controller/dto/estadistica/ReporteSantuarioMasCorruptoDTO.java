package ar.edu.unq.epersgeist.controller.dto.estadistica;

import ar.edu.unq.epersgeist.controller.dto.medium.RecuperarMediumDTO;
import ar.edu.unq.epersgeist.modelo.estadistica.ReporteSantuarioMasCorrupto;

public record ReporteSantuarioMasCorruptoDTO(String nombreSantuario,
                                             RecuperarMediumDTO medium,
                                             Integer cantidadDeDemonios,
                                             Integer cantidadDeDemoniosLibres) {

    public static ReporteSantuarioMasCorruptoDTO desdeModelo(ReporteSantuarioMasCorrupto reporteSantuarioMasCorrupto) {
        return new ReporteSantuarioMasCorruptoDTO(
                reporteSantuarioMasCorrupto.nombreSantuario(),
                RecuperarMediumDTO.desdeModelo(reporteSantuarioMasCorrupto.mediumConMasDemonios()),
                reporteSantuarioMasCorrupto.cantidadTotalDemonios(),
                reporteSantuarioMasCorrupto.cantidadDemoniosLibres()
        );
    }

}
