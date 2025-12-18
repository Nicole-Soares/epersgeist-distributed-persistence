package ar.edu.unq.epersgeist.service.interfaces;

import ar.edu.unq.epersgeist.modelo.estadistica.ReportePromedio;
import ar.edu.unq.epersgeist.modelo.estadistica.ReporteSantuarioMasCorrupto;

import java.util.List;

public interface EstadisticaService {
    ReporteSantuarioMasCorrupto santuarioCorrupto();
    List<ReportePromedio> obtenerPromedioPorTipoSensor();
}
