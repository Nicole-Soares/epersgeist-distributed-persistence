package ar.edu.unq.epersgeist.persistence.repository.impl;

import ar.edu.unq.epersgeist.exception.UbicacionesNoConectadasException;
import ar.edu.unq.epersgeist.modelo.estadistica.CantidadDeDemoniosEnUnSantuario;
import ar.edu.unq.epersgeist.modelo.estadistica.ReportePromedio;
import ar.edu.unq.epersgeist.modelo.estadistica.SantuarioMasCorrupto;
import ar.edu.unq.epersgeist.modelo.sensor.DatoSensorNormalizado;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistence.mapper.interfaces.UbicacionMapper;
import ar.edu.unq.epersgeist.persistence.mongo.entity.UbicacionMongo;
import ar.edu.unq.epersgeist.persistence.mongo.interfaces.UbicacionMongoDAO;
import ar.edu.unq.epersgeist.persistence.neo.ComunicacionActivaNeo4JDao;
import ar.edu.unq.epersgeist.persistence.neo.UbicacionNeo4JDAO;
import ar.edu.unq.epersgeist.persistence.neo.entity.UbicacionNeo4J;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.persistence.sql.interfaces.UbicacionSQLDAO;
import ar.edu.unq.epersgeist.persistence.sql.entity.UbicacionSQL;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class UbicacionRepositoryImpl implements UbicacionRepository {

    private final UbicacionNeo4JDAO ubicacionNeo4JDAO;
    private final UbicacionSQLDAO ubicacionSQLDAO;
    private final UbicacionMongoDAO ubicacionMongoDAO;
    private final UbicacionMapper ubicacionMapper;
    private final ComunicacionActivaNeo4JDao comunicacionActivaNeo4JDao;

    public UbicacionRepositoryImpl(UbicacionNeo4JDAO ubicacionNeo4JDAO, UbicacionSQLDAO ubicacionSQLDAO, UbicacionMongoDAO ubicacionMongoDAO, UbicacionMapper ubicacionMapper, ComunicacionActivaNeo4JDao comunicacionActivaNeo4JDao) {
        this.ubicacionNeo4JDAO = ubicacionNeo4JDAO;
        this.ubicacionSQLDAO = ubicacionSQLDAO;
        this.ubicacionMongoDAO = ubicacionMongoDAO;
        this.ubicacionMapper = ubicacionMapper;
        this.comunicacionActivaNeo4JDao = comunicacionActivaNeo4JDao;
    }

    @Override
    public Ubicacion save(Ubicacion ubicacion) {
        //persistencia a sql
        UbicacionSQL ubicacionSQL = ubicacionMapper.aEntidad(ubicacion);
        UbicacionSQL ubicacionGuardada = this.ubicacionSQLDAO.save(ubicacionSQL);
        ubicacion.setId(ubicacionGuardada.getId());

        //persistencia de nodo base a neo4j
        UbicacionNeo4J ubicacionNeo4J = ubicacionMapper.aEntidadNeo4J(ubicacion);
        this.ubicacionNeo4JDAO.save(ubicacionNeo4J.getId());

        //persistencia de conexiones a neo4j
        for (Map.Entry<Long, Long> entry : ubicacion.getConexiones().entrySet()){
            Long idDestino = entry.getKey();
            Long costo = entry.getValue();
            this.ubicacionNeo4JDAO.conectar(ubicacion.getId(), idDestino, costo);
        }

        //se recupera el nodo actualizado con sus relaciones
        UbicacionNeo4J ubicacionNeo4JActualizada = this.ubicacionNeo4JDAO.findById(ubicacion.getId());

        //Persistencia en mongo
        String ubicacionMongoId = ubicacion.getId().toString();

        UbicacionMongo ubicacionMongoGuardada = persistirMongo(ubicacion, ubicacionMongoId);

        return ubicacionMapper.aModelo(ubicacionGuardada, ubicacionNeo4JActualizada, ubicacionMongoGuardada);
    }

    private UbicacionMongo persistirMongo(Ubicacion ubicacion, String ubicacionMongoId) {
        UbicacionMongo ubicacionMongo;
        Optional<UbicacionMongo> ubicacionExistenteOptional = this.ubicacionMongoDAO.findById(ubicacionMongoId);

        GeoJsonPolygon nuevaArea = ubicacionMapper.aEntidadSecundaria(ubicacion).getArea();

        if (ubicacionExistenteOptional.isPresent()) {
            // Actualizo el area por las dudas, las coordenadas las actualizan las entidades
            ubicacionMongo = ubicacionExistenteOptional.get();
            ubicacionMongo.setArea(nuevaArea);
        } else {
            // Creo la ubicacionMongo si no existe
            ubicacionMongo = ubicacionMapper.aEntidadSecundaria(ubicacion);
        }

        return this.ubicacionMongoDAO.save(ubicacionMongo);
    }


    @Override
    public Ubicacion findByIdSinConexiones(Long ubicacionId) {
        UbicacionSQL ubicacionSQL = this.ubicacionSQLDAO.findById(ubicacionId).orElseThrow(() -> new EntityNotFoundException("No se encontro el ubicacion con id: " + ubicacionId));
        UbicacionMongo ubicacionMongo = this.ubicacionMongoDAO.findById(ubicacionId.toString()).orElseThrow(
                () -> new EntityNotFoundException("No se encontró la ubicación con ID: " + ubicacionId));
        return ubicacionMapper.aModelo(ubicacionSQL, ubicacionMongo);
    }


    @Override
    public Ubicacion findByIdConConexiones(Long id) {
        UbicacionSQL ubicacionSQL = ubicacionSQLDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró ubicación SQL con id " + id));
        UbicacionNeo4J ubicacionNeo = ubicacionNeo4JDAO.findById(id);
        UbicacionMongo ubicacionMongo = this.ubicacionMongoDAO.findById(id.toString()).orElseThrow(
                () -> new EntityNotFoundException("No se encontró la ubicación con ID: " + id));
        return ubicacionMapper.aModelo(ubicacionSQL, ubicacionNeo, ubicacionMongo);
    }

    @Override
    public List<Ubicacion> findAll() {
        List<UbicacionSQL> ubicacionesSQL = this.ubicacionSQLDAO.findAll();
        return findAllUbicacionesMapeadasConConexiones(ubicacionesSQL);
    }


    private List<Ubicacion> findAllUbicacionesMapeadasConConexiones(List<UbicacionSQL> ubicacionesSQL) {
        return ubicacionesSQL.stream()
                .map(ubicacionSQL -> {
                    UbicacionNeo4J ubicacionNeo4J = this.ubicacionNeo4JDAO.findById(ubicacionSQL.getId());
                    UbicacionMongo ubicacionMongo = this.ubicacionMongoDAO.findById(ubicacionSQL.getId().toString()).orElseThrow(
                            () -> new EntityNotFoundException("No se encontró la ubicación con ID: " + ubicacionSQL.getId()));
                    return ubicacionMapper.aModelo(ubicacionSQL, ubicacionNeo4J, ubicacionMongo);
                })
                .toList();
    }

    @Override
    public void deleteById(Long ubicacionId) {
        this.ubicacionSQLDAO.deleteById(ubicacionId);
        this.ubicacionNeo4JDAO.deleteById(ubicacionId);
        this.ubicacionMongoDAO.deleteById(ubicacionId.toString());
    }

    @Override
    public SantuarioMasCorrupto findSantuarioMasCorruptoYMedium() {
        return this.ubicacionSQLDAO.findSantuarioMasCorruptoYMedium();
    }

    @Override
    public CantidadDeDemoniosEnUnSantuario cantidadDeDemoniosEnElSantuario(Long idSantuario) {
        return this.ubicacionSQLDAO.cantidadDeDemoniosEnElSantuario(idSantuario);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return this.ubicacionSQLDAO.existsByNombre(nombre);
    }

    @Override
    public boolean solapaOtraUbicacion(Set<Coordenadas> vertices) {
        GeoJsonPolygon poligono = this.ubicacionMapper.aPoligono(vertices);
        return this.ubicacionMongoDAO.existsByAreaIntersecting(poligono);
    }

    @Override
    public boolean estanConectadasPorUnPasoDeDistancia(Long idOrigen, Long idDestino) {
        return ubicacionNeo4JDAO.estadoConexion(idOrigen, idDestino);
    }

    /**
     * Retorna el camino más rentable entre dos ubicaciones. Tira UbicacionesNoConectadasException si no hay conexión entre ellas.
     * @param idOrigen El ID de la ubicación de origen.
     * @param idDestino El ID de la ubicación de destino.
     * @return Una lista de ubicaciones que representan el camino más rentable. Estas ubicaciones incluyen unicamente las conexiones hacia la siguiente ubicación en el camino.
     */
    @Override
    public List<Ubicacion> caminoMasRentable(Long idOrigen, Long idDestino) {
        ubicacionNeo4JDAO.eliminarProyeccionGrafoSiExiste();

        ubicacionNeo4JDAO.proyectarGrafo();

        List<UbicacionNeo4J> caminoMasRentable = ubicacionNeo4JDAO.caminoMasRentable(idOrigen, idDestino);

        ubicacionNeo4JDAO.eliminarProyeccionGrafoSiExiste();

        if (caminoMasRentable.isEmpty()) throw new UbicacionesNoConectadasException(idOrigen, idDestino);

        return caminoMasRentable.stream()
                .map(ubicacionNeo4J -> {
                    UbicacionSQL ubicacionSQL = this.ubicacionSQLDAO.findById(ubicacionNeo4J.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Ubicación SQL no encontrada para ID: " + ubicacionNeo4J.getId()));
                    UbicacionMongo ubicacionMongo = this.ubicacionMongoDAO.findById(ubicacionNeo4J.getId().toString()).orElseThrow(
                            () -> new EntityNotFoundException("No se encontró la ubicación con ID: " + ubicacionNeo4J));
                    return ubicacionMapper.aModelo(ubicacionSQL, ubicacionNeo4J, ubicacionMongo);
                })
                .toList();
    }
          
    @Override
    public List<Ubicacion> caminoMasCorto(Long idOrigen, Long idDestino){
            return ubicacionNeo4JDAO.caminoMasCorto(idOrigen, idDestino)
                    .stream()
                    .map(ubicacionNeo4J -> {
                        UbicacionSQL ubicacionSQL = this.ubicacionSQLDAO.findById(ubicacionNeo4J.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Ubicación SQL no encontrada para ID: " + ubicacionNeo4J.getId()));
                        UbicacionMongo ubicacionMongo = this.ubicacionMongoDAO.findById(ubicacionNeo4J.getId().toString()).orElseThrow(
                                () -> new EntityNotFoundException("No se encontró la ubicación con ID: " + ubicacionNeo4J));
                        return ubicacionMapper.aModelo(ubicacionSQL, ubicacionNeo4J, ubicacionMongo);
                    })
                    .toList();
    }

    @Override
    public List<Ubicacion> ubicacionesSobrecargadas(Integer umbralDeEnergia) {
        List<UbicacionSQL> ubicacionesSQL = ubicacionSQLDAO.findByFlujoEnergiaGreaterThan(umbralDeEnergia);
        return ubicacionesSQL.stream()
                .map( ubicacionSQL -> {
                    UbicacionMongo ubicacionMongo = this.ubicacionMongoDAO.findById(ubicacionSQL.getId().toString()).orElseThrow(
                            () -> new EntityNotFoundException("No se encontró la ubicación con ID: " + ubicacionSQL.getId()));
                    return ubicacionMapper.aModelo(ubicacionSQL, ubicacionMongo);
                })
                .toList();
    }

    @Override
    public Ubicacion findByCoordenadas(Double latitud, Double longitud) {
        // Crear punto GeoJSON
        GeoJsonPoint punto = new GeoJsonPoint(longitud, latitud);

        // Buscar la ubicación Mongo cuyo área contenga ese punto
        UbicacionMongo ubicacionMongo = ubicacionMongoDAO.findByPuntoDentroDelArea(punto);

        if (ubicacionMongo == null) {
            throw new EntityNotFoundException(
                    "No se encontró la ubicación que contenga las coordenadas (" + latitud + ", " + longitud + ")"
            );
        }
            // pasamos a long para buscar por id la ubicación SQL y Neo4j
        Long ubicacionId = Long.parseLong(ubicacionMongo.getId());

        UbicacionSQL ubicacionSQL = ubicacionSQLDAO.findById(ubicacionId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró ubicación SQL con ID: " + ubicacionId));

        UbicacionNeo4J ubicacionNeo = ubicacionNeo4JDAO.findById(ubicacionId);

        return ubicacionMapper.aModelo(ubicacionSQL, ubicacionNeo, ubicacionMongo);
    }

    @Override
    public List<ReportePromedio> obtenerPromedioPorTipoSensor() {
        return ubicacionMongoDAO.obtenerPromedioPorTipoSensor();
    }

    @Override
    public void normalizacionDeDocumento() {
        this.ubicacionMongoDAO.normalizacionDeDocumento();
    }

    @Override
    public List<DatoSensorNormalizado> findAllNormalizados() {
        return this.ubicacionMongoDAO.findAllNormalizados();
    }

    @Override
    public Long findEspirituIdConComunicacionActivaConMediumById(Long mediumId) {
        Long espirituId = comunicacionActivaNeo4JDao.findEspirituIdByMediumId(mediumId);

        if (espirituId == null) {
            throw new EntityNotFoundException(
                    "El Medium con id '"+ mediumId +"' no tiene entablada ninguna comunicación activa actualmente"
            );
        }

        return espirituId;
    }
}