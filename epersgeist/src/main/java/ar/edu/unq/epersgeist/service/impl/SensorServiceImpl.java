package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.epersgeist.modelo.sensor.DatoSensorNormalizado;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.service.interfaces.SensorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorServiceImpl implements SensorService {

    private final UbicacionRepository ubicacionRepository;

    public SensorServiceImpl(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    @Override
    public List<DatoSensorNormalizado> ejecutarNormalizacionYConsultar() {
        this.ubicacionRepository.normalizacionDeDocumento();
        return ubicacionRepository.findAllNormalizados();
    }
}