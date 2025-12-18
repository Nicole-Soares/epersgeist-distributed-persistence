package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.epersgeist.exception.NoExistenSantuarioCorrompidoException;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.estadistica.CantidadDeDemoniosEnUnSantuario;
import ar.edu.unq.epersgeist.modelo.estadistica.ReportePromedio;
import ar.edu.unq.epersgeist.modelo.estadistica.ReporteSantuarioMasCorrupto;
import ar.edu.unq.epersgeist.modelo.estadistica.SantuarioMasCorrupto;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.MediumRepository;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.service.interfaces.EstadisticaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EstadisticaServiceImpl implements EstadisticaService {

    private final UbicacionRepository ubicacionRepository;
    private final MediumRepository mediumRepository;

    public EstadisticaServiceImpl(UbicacionRepository ubicacionRepository, MediumRepository mediumRepository) {
        this.ubicacionRepository = ubicacionRepository;
        this.mediumRepository = mediumRepository;
    }

    /**
     * Obtiene el reporte del santuario más corrupto.
     * @return ReporteSantuarioMasCorrupto con detalles del santuario más corrupto.
     */
    @Override
    public ReporteSantuarioMasCorrupto santuarioCorrupto() {
        SantuarioMasCorrupto santuarioCorrompido = ubicacionRepository.findSantuarioMasCorruptoYMedium();
        validarExisteSantuarioCorrompido(santuarioCorrompido);
        Medium mediumCorrompido = mediumRepository.findById(santuarioCorrompido.getIdMediumConMasDemonios());
        CantidadDeDemoniosEnUnSantuario cantidadDeDemonios = ubicacionRepository.cantidadDeDemoniosEnElSantuario(santuarioCorrompido.getIdSantuario());
        return new ReporteSantuarioMasCorrupto(cantidadDeDemonios.nombreSantuario(), mediumCorrompido, (Integer) (cantidadDeDemonios.cantidadTotalDemonios()).intValue(), (Integer) cantidadDeDemonios.cantidadDemoniosLibres().intValue());
    }

    private static void validarExisteSantuarioCorrompido(SantuarioMasCorrupto santuarioCorrompido) {
        if (santuarioCorrompido == null || santuarioCorrompido.getIdMediumConMasDemonios() == null) {throw new NoExistenSantuarioCorrompidoException();}
    }

    @Override
    public List<ReportePromedio> obtenerPromedioPorTipoSensor() {
        return ubicacionRepository.obtenerPromedioPorTipoSensor();
    }
}
