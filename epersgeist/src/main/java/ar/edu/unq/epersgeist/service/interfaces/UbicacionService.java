package ar.edu.unq.epersgeist.service.interfaces;

import ar.edu.unq.commons.dto.TemperaturaDTO;
import ar.edu.unq.epersgeist.controller.dto.ubicacion.CrearUbicacionDTO;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;

import java.util.List;

public interface UbicacionService {
    /**
     * Crea una nueva Ubicacion en la base de datos en base a la ubicacion recibida.
     * @param ubicacion La Ubicacion a ser creada.
     * @return La Ubicacion creada.
     */
    Ubicacion create(Ubicacion ubicacion);

    Ubicacion createDTO(CrearUbicacionDTO ubicacionDTO);

    /**
     * Recupera una Ubicacion de la base de datos en base al ID recibido.
     * @param ubicacionId El ID de la Ubicacion a ser recuperada.
     * @return La Ubicacion recuperada.
     */
    Ubicacion findByIdSinConexion(Long ubicacionId);

    /**
     * Recupera una Ubicacion con sus conexiones de la base de datos en base al ID recibido.
     * @param ubicacionId El ID de la Ubicacion a ser recuperada.
     * @return La Ubicacion con sus conexiones.
     */
    Ubicacion findByIdConConexiones(Long ubicacionId);

    /**
     * Recupera todas las ubicaciones de la base de datos.
     *
     * @return una lista de ubicaciones recuperadas.
     */
    List<Ubicacion> recuperarTodos();

    /**
     * Actualiza una Ubicacion en la base de datos en base a la Ubicacion recibida.
     * @param ubicacion La ubicacion a ser actualizada.
     * @return La Ubicacion actualizada.
     */
    Ubicacion update(Ubicacion ubicacion);

    /**
     * Elimina una Ubicacion de la base de datos en base al ID recibido. Si no existe en la base de datos, no hace nada.
     * @param ubicacionId El ID de la Ubicacion a ser eliminada.
     */
    void delete(Long ubicacionId);

    /**
     * Retorna los espíritus existentes en la ubicación dada.
     * @param ubicacionId el ID de la ubicacion a evaluar.
     */
    List<Espiritu> espiritusEn(Long ubicacionId);

    /**
     * Retorna los médiums posicionados en la ubicación dada que no hayan conectado con ningún
     * espíritu.
     * @param ubicacionId el ID de la ubicación a evaluar.
     */
    List<Medium> mediumsSinEspiritusEn(Long ubicacionId);

    /**
     * Conecta dos ubicaciones existentes, de origen hacia destino con un costo dado.
     * @param idOrigen el ID de la ubicación origen de la conexion.
     * @param idDestino el ID de la ubicacion destino
     * @param costo el costo de la conexion.
     */
    void conectar(Long idOrigen, Long idDestino, Long costo);

    /**
     * Denota verdadero si la ubicacion origen esta conectada con la ubicacion destino por un paso de distancia.
     * @param idOrigen el ID de la ubicación origen de la conexion.
     * @param idDestino el ID de la ubicacion destino
     */
    boolean estanConectadas(Long idOrigen, Long idDestino);
  
    /**
     * Retorna el camino más rentable entre dos ubicaciones.
     * @param idOrigen El ID de la ubicación de origen.
     * @param idDestino El ID de la ubicación de destino.
     * @return Una lista de ubicaciones que representan el camino más rentable.
     */
    List<Ubicacion> caminoMasRentable(Long idOrigen, Long idDestino);

    /**
     * Encuentra el camino más corto entre dos ubicaciones dadas por sus IDs.*
     * @param idOrigen  El ID de la ubicación de origen.
     * @param idDestino El ID de la ubicación de destino.
     * @return Una lista de Ubicaciones que representan el camino más corto desde el origen hasta el destino.
     */
    List<Ubicacion> caminoMasCorto(Long idOrigen, Long idDestino);
  
    /**
     * Retorna las ubicaciones que poseen una cantidad de energia superior al umbral dado.
     * @param umbralDeEnergia el umbral a evaluar.
     */
    List<Ubicacion> ubicacionesSobrecargadas(Integer umbralDeEnergia);

    void actualizarTemperaturaDelConsumer(TemperaturaDTO dto);
}