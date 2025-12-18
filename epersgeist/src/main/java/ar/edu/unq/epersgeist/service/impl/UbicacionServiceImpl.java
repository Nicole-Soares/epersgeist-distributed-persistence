package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.commons.dto.TemperaturaDTO;
import ar.edu.unq.epersgeist.controller.dto.ubicacion.CrearUbicacionDTO;
import ar.edu.unq.epersgeist.exception.NombreRepetidoException;
import ar.edu.unq.epersgeist.exception.UbicacionSolapadaException;
import ar.edu.unq.epersgeist.exception.UbicacionesNoConectadasException;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.CoordenadaRepository;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.EspirituRepository;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.MediumRepository;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final MediumRepository mediumRepository;
    private final EspirituRepository espirituRepository;

    /**
     * Constructor de Impl.
     * @param ubicacionRepository El DAO de Ubicacion a ser utilizado.
     */
    public UbicacionServiceImpl(UbicacionRepository ubicacionRepository, MediumRepository mediumRepository, EspirituRepository espirituRepository, CoordenadaRepository coordenadaRepository) {
        this.ubicacionRepository = ubicacionRepository;
        this.mediumRepository = mediumRepository;
        this.espirituRepository = espirituRepository;
    }

    /**
     * Crea una nueva Ubicacion en la base de datos en base a la Ubicacion recibida.
     * @param ubicacion La Ubicacion a ser creada.
     * @return La Ubicacion creada.
     */
    @Override
    public Ubicacion create(Ubicacion ubicacion) {
        if(this.ubicacionRepository.existsByNombre(ubicacion.getNombre())){
            throw new NombreRepetidoException(ubicacion.getNombre());
        }
        if(this.ubicacionRepository.solapaOtraUbicacion(ubicacion.getVertices())){
            throw new UbicacionSolapadaException(ubicacion.getNombre()  );
        }
        return this.ubicacionRepository.save(ubicacion);
    }

    public Ubicacion createDTO(CrearUbicacionDTO ubicacionDTO) {
        Ubicacion ubicacion = ubicacionDTO.aModelo();
        return this.ubicacionRepository.save(ubicacion);
    }

    /**
     * Recupera una Ubicacion de la base de datos en base al ID recibido.
     * @param ubicacionId El ID de la Ubicacion a ser recuperada.
     * @return La Ubicacion recuperada.
     */
    @Override
    public Ubicacion findByIdSinConexion(Long ubicacionId) {
        return this.ubicacionRepository.findByIdSinConexiones(ubicacionId);
    }

    /**
     * Recupera una Ubicacion con sus conexiones de la base de datos en base al ID recibido.
     * @param ubicacionId El ID de la Ubicacion a ser recuperada.
     * @return La Ubicacion con sus conexiones.
     */
    @Override
    public Ubicacion findByIdConConexiones(Long ubicacionId){
        return this.ubicacionRepository.findByIdConConexiones(ubicacionId);
    }

    /**
     * Actualiza una Ubicacion en la base de datos en base a la Ubicacion recibida.
     * @param ubicacion La Ubicacion a ser actualizada.
     * @return La Ubicacion actualizada.
     */
    @Override
    public Ubicacion update(Ubicacion ubicacion) {
        return this.ubicacionRepository.save(ubicacion);
    }

    /**
     * Elimina una Ubicacion de la base de datos en base al ID recibido. Si no existe en la base de datos, no hace nada.
     * @param ubicacionId El ID de la Ubicacion a ser eliminada.
     */
    @Override
    public void delete(Long ubicacionId) {
        this.ubicacionRepository.deleteById(ubicacionId);
    }

    /**
     * Retorna los espíritus existentes en la ubicación dada.
     * @param ubicacionId el ID de la ubicacion a evaluar.
     */
    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        return this.espirituRepository.findByUbicacionId(ubicacionId);
    }

    /**
     * Retorna los médiums posicionados en la ubicación dada que no hayan conectado con ningún
     * espíritu.
     * @param ubicacionId el ID de la ubicación a evaluar.
     */
    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
        return this.mediumRepository.findByUbicacionIdAndEspiritusIsEmpty(ubicacionId);
    }

    /**
     * Conecta dos ubicaciones existentes, de origen hacia destino con un costo dado.
     * @param idOrigen el ID de la ubicación origen de la conexion.
     * @param idDestino el ID de la ubicacion destino
     * @param costo el costo de la conexion.
     */
    @Override
    public void conectar(Long idOrigen, Long idDestino, Long costo) {
        Ubicacion origen = ubicacionRepository.findByIdSinConexiones(idOrigen);
        Ubicacion destino = ubicacionRepository.findByIdSinConexiones(idDestino);
        origen.conectarCon(destino, costo);
        this.ubicacionRepository.save(origen);
    }

    /**
     * Denota verdadero si la ubicacion origen esta conectada con la ubicacion destino por un paso de distancia.
     * @param idOrigen el ID de la ubicación origen de la conexion.
     * @param idDestino el ID de la ubicacion destino
     */
    @Override
    public boolean estanConectadas(Long idOrigen, Long idDestino) {
        return this.ubicacionRepository.estanConectadasPorUnPasoDeDistancia(idOrigen, idDestino);
    }

    /**
     * Recupera todas las ubicaciones de la base de datos.
     * @return una lista de ubicaciones recuperadas.
     */
    @Override
    public List<Ubicacion> recuperarTodos() {
        return this.ubicacionRepository.findAll();
    }

    /**
     * Retorna el camino más rentable entre dos ubicaciones.
     * @param idOrigen El ID de la ubicación de origen.
     * @param idDestino El ID de la ubicación de destino.
     * @return Una lista de ubicaciones que representan el camino más rentable. Estas ubicaciones incluyen unicamente las conexiones hacia la siguiente ubicación en el camino.
     */
    @Override
    public List<Ubicacion> caminoMasRentable(Long idOrigen, Long idDestino) {
        return ubicacionRepository.caminoMasRentable(idOrigen, idDestino);
    }

    @Override
    public List<Ubicacion> caminoMasCorto(Long idOrigen, Long idDestino) {
        List<Ubicacion> caminoMasCorto = this.ubicacionRepository.caminoMasCorto(idOrigen, idDestino);
        if (caminoMasCorto.isEmpty()) {
            throw new UbicacionesNoConectadasException(idOrigen, idDestino);
        } else {
            return caminoMasCorto;
        }
    }

    /**
     * Retorna las ubicaciones que poseen una cantidad de energia superior al umbral dado.
     * @param umbralDeEnergia el umbral a evaluar.
     */
    @Override
    public List<Ubicacion> ubicacionesSobrecargadas(Integer umbralDeEnergia){
        return ubicacionRepository.ubicacionesSobrecargadas(umbralDeEnergia);
    }

    @Override
    public void actualizarTemperaturaDelConsumer(TemperaturaDTO dto) {
        Ubicacion ubicacion = ubicacionRepository.findByIdSinConexiones(dto.ubicacionId());
        ubicacion.setTemperatura(dto.temperatura());
        ubicacionRepository.save(ubicacion);
    }
}