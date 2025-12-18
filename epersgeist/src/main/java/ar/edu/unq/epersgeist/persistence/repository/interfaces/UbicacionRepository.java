package ar.edu.unq.epersgeist.persistence.repository.interfaces;
import ar.edu.unq.epersgeist.modelo.estadistica.CantidadDeDemoniosEnUnSantuario;
import ar.edu.unq.epersgeist.modelo.estadistica.ReportePromedio;
import ar.edu.unq.epersgeist.modelo.estadistica.SantuarioMasCorrupto;
import ar.edu.unq.epersgeist.modelo.sensor.DatoSensorNormalizado;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import java.util.List;
import java.util.Set;

public interface UbicacionRepository {
    Ubicacion save(Ubicacion ubicacion);
    Ubicacion findByIdSinConexiones(Long ubicacionId);
    Ubicacion findByIdConConexiones(Long id);
    List<Ubicacion> findAll();
    void deleteById(Long ubicacionId);
    SantuarioMasCorrupto findSantuarioMasCorruptoYMedium();
    CantidadDeDemoniosEnUnSantuario cantidadDeDemoniosEnElSantuario(Long idSantuario);
    boolean existsByNombre(String nombre);
    boolean solapaOtraUbicacion(Set<Coordenadas> vertices);
    boolean estanConectadasPorUnPasoDeDistancia(Long idOrigen, Long idDestino);
    List<Ubicacion> caminoMasRentable(Long idOrigen, Long idDestino);
    List<Ubicacion> caminoMasCorto(Long idOrigen, Long idDestino);
    List<Ubicacion> ubicacionesSobrecargadas(Integer umbralDeEnergia);
    Ubicacion findByCoordenadas(Double latitud, Double longitud);
    List<ReportePromedio> obtenerPromedioPorTipoSensor();
    void normalizacionDeDocumento();
    List<DatoSensorNormalizado> findAllNormalizados();
    Long findEspirituIdConComunicacionActivaConMediumById(Long mediumId);
}
