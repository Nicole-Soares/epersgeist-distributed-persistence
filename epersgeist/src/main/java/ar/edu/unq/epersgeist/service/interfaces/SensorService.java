package ar.edu.unq.epersgeist.service.interfaces;

import ar.edu.unq.epersgeist.modelo.sensor.DatoSensorNormalizado;

import java.util.List;

public interface SensorService {

    List<DatoSensorNormalizado> ejecutarNormalizacionYConsultar();
}
