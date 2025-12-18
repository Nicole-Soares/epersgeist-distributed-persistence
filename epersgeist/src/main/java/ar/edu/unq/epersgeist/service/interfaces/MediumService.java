package ar.edu.unq.epersgeist.service.interfaces;

import ar.edu.unq.epersgeist.controller.dto.medium.ActualizarMediumDTO;
import ar.edu.unq.epersgeist.controller.dto.medium.CrearMediumDTO;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.List;

public interface MediumService {

    /**
     * Crea un nuevo Medium en la base de datos en base al Medium recibido.
     * @param medium El Medium a ser creado.
     * @return El Medium creado.
     */
    Medium create(Medium medium);

    Medium create(CrearMediumDTO mediumDTO);

    /**
     * Recupera un Medium de la base de datos en base al ID recibido.
     * @param mediumId El ID del Medium a ser recuperado.
     * @return El Medium recuperado.
     */
    Medium findById(Long mediumId);


    /**
     * Actualiza un Medium en la base de datos en base al Medium recibido.
     * @param medium El Medium a ser actualizado.
     * @return El Medium actualizado.
     */
    Medium update(Medium medium);

    Medium actualizarMedium(Long id, ActualizarMediumDTO mediumDTO);

    /**
     * Elimina un Medium de la base de datos en base al ID recibido. Si no existe en la base de datos, no hace nada.
     * @param mediumId El ID del Medium a ser eliminado.
     */
    void delete(Long mediumId);


    /**
     * Recupera todos los Mediums en la base de datos.
     * @return Una lista de Mediums recuperados.
     */
    List<Medium> findAll();


    /**
     * Recupera 15 puntos de mana y cada espiritu conectado a el recuperara 5 puntos de conexion.
     *          Si no tiene espiritus recupera el mana solamente y si tiene mana al max tira excepcion.
     * @param mediumId El ID del Medium que se quiere poner a descansar.
     */
    void descansar(Long mediumId);


    /**
     * Recupera todos los espiritus de la base de datos que esten conectados al Medium recibido.
     * @param mediumId El ID del Medium del cual se quieren conocer los espiritus.
     * @return Una lista de espiritus que estan conectados al Medium.
     */
    List<Espiritu> espiritus(Long mediumId);


    /**
     * Cambia la ubicacion del Espiritu a la del Medium siempre y cuando el Espiritu este libre y el Medium tenga suficiente mana.
     * @param mediumId El ID del Medium del cual realiza la invocacion y el espirituId al cual se quiere invocar.
     * @return Un Espiritu con su ubicacion cambiada a la ubicacion del Medium, si el Espiritu no esta libre, tira una excepcion .
     */
    Espiritu invocar(Long mediumId, Long espirituId);


    /**
     * Un Medium exorciza a otro Medium.
     * @param idMediumExorcista El ID del Medium que exorciza.
     * @param idMediumAExorcizar El ID del Medium a ser exorcizado.
     */
    void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar);


    /**
     * Cambia la ubicacion del medium a la ubicacion recibida siempre y cuando este conectada directamente a la ubicacion actual del medium.
     *  De no estarlo tira una excepcion.
     * @param mediumId El ID del Medium que se va a mover.
     * @param latitud La latitud de la ubicacion a la cual quiere moverse.
     * @param longitud la longitud de la ubicacion a la cual quiere moverse.
     */
    void mover(Long mediumId, Double latitud, Double longitud);
    void reducirCordura(Long mediumId, Long espirituId);
}
