package ar.edu.unq.servicio_temperatura.service;

import ar.edu.unq.commons.dto.TemperaturaDTO;
import ar.edu.unq.servicio_temperatura.persistence.sql.dao.TemperaturaEspirituReadDAO;
import ar.edu.unq.servicio_temperatura.persistence.sql.projection.HostilidadPorUbicacionProjection;
import ar.edu.unq.servicio_temperatura.producer.TemperaturaProducer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class TemperaturaService {

    private final TemperaturaProducer producer;
    private final TemperaturaEspirituReadDAO dao;
    private final Random random = new Random();

    public TemperaturaService(TemperaturaProducer producer,
                              TemperaturaEspirituReadDAO dao) {
        this.producer = producer;
        this.dao = dao;
    }

    //-------------- Lógica para ubicaciones hostiles ----------------//

    public void enviarTemperaturasPorHostilidad() {
        List<HostilidadPorUbicacionProjection> ubicaciones =
                dao.getHostilidadAgrupada();

        System.out.println(">>> Ubicaciones leídas: " + ubicaciones.size());

        for (HostilidadPorUbicacionProjection u : ubicaciones) {
            int temperatura = calcularTemperaturaSegunHostilidad(u.getHostilidadTotal());

            System.out.println(">>> Calculada temp=" + temperatura + " para ubicacion " + u.getUbicacionId());

            producer.enviarTemperatura(new TemperaturaDTO(u.getUbicacionId(), temperatura));
        }
    }

    //-------------- Lógica para ubicaciones liberadas ----------------//

    public void enviarTemperaturaSinEspiritus(Long ubicacionId) {
        int temp = generarTemperaturaSinEspiritus();

        System.out.println("[temperatura] Ubicación " + ubicacionId +
                " sin espíritus → temperatura=" + temp);

        producer.enviarTemperatura(new TemperaturaDTO(ubicacionId, temp));
    }

    //-------------- Cálculo de temperatura para ubicaciones hostiles----------------//

    private int calcularTemperaturaSegunHostilidad(Double hostilidadTotal) {

        final double HOSTILIDAD_MIN = 0.0;
        final double HOSTILIDAD_MAX = 100.0;

        final double TEMP_MIN = -10.0;
        final double TEMP_MAX = 20.0;

        final double VARIANZA = 1.5;

        double hostilidad = Math.max(HOSTILIDAD_MIN, Math.min(HOSTILIDAD_MAX, hostilidadTotal));
        double h = hostilidad / HOSTILIDAD_MAX;

        double mediaMax = TEMP_MAX - VARIANZA;
        double mediaMin = TEMP_MIN + VARIANZA;

        double media = mediaMax + (mediaMin - mediaMax) * h;

        double minTemp = media - VARIANZA;
        double r = Math.random();
        double resultado = minTemp + r * (VARIANZA * 2);

        return (int) Math.round(resultado);
    }

    //-------------- Cálculo de temperatura para ubicaciones liberadas ----------------//

    private int generarTemperaturaSinEspiritus() {
        int min = 21;
        int max = 24;
        return random.nextInt(max - min + 1) + min;
    }
}
