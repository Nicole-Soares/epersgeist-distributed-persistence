package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.epersgeist.controller.dto.medium.ActualizarMediumDTO;
import ar.edu.unq.epersgeist.controller.dto.medium.CrearMediumDTO;
import ar.edu.unq.epersgeist.exception.DistanciaMaximaParaInvocacion;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistence.repository.impl.CalculadorDeDistanciaRepositoryImpl;
import ar.edu.unq.epersgeist.persistence.repository.interfaces.*;
import ar.edu.unq.epersgeist.service.interfaces.MediumService;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MediumServiceImpl implements MediumService {

    private final MediumRepository mediumRepository;
    private final EspirituRepository espirituRepository;
    private final UbicacionRepository ubicacionRepository;
    private final CoordenadaRepository coordenadaRepository;
    private final CalculadorDeDistanciaRepository calculadorDeDistanciaRepository;

    /**
     * Constructor de MediumServiceImpl.
     * @param mediumRepository El DAO de Medium a ser utilizado.
     */
    public MediumServiceImpl(MediumRepository mediumRepository, EspirituRepository espirituRepository, UbicacionRepository ubicacionRepository, CoordenadaRepository coordenadaRepository, CalculadorDeDistanciaRepository calculadorDeDistanciaRepository) {
        this.mediumRepository = mediumRepository;
        this.espirituRepository = espirituRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.coordenadaRepository = coordenadaRepository;
        this.calculadorDeDistanciaRepository = calculadorDeDistanciaRepository;
    }

    /**
     * Crea un nuevo Medium en la base de datos en base al Medium recibido.
     * @param medium El Medium a ser creado.
     * @return El Medium creado.
     */
    @Override
    public Medium create(Medium medium) {
        return mediumRepository.save(medium);
    }

    @Override
    public Medium create(CrearMediumDTO mediumDTO) {
        Ubicacion ubicacionMedium = ubicacionRepository.findByIdSinConexiones(mediumDTO.ubicacionId());
        Medium medium = mediumDTO.aModelo(ubicacionMedium);
        return mediumRepository.save(medium);
    }

    /**
     * Recupera un Medium de la base de datos en base al ID recibido.
     * @param mediumId El ID del Medium a ser recuperado.
     * @return El Medium recuperado.
     */

    @Override
    public Medium findById(Long mediumId) {
        return this.mediumRepository.findById(mediumId);
    }

    /**
     * Actualiza un Medium en la base de datos en base al Medium recibido.
     * @param medium El Medium a ser actualizado.
     * @return El Medium actualizado.
     */
    @Override
    public Medium update(Medium medium) {
        return this.mediumRepository.save(medium);
    }


    /**
     * Actualiza un Medium en la base de datos en base al ID y los datos recibidos.
     * @param id El ID del Medium a ser actualizado.
     * @param mediumDTO Los datos a ser actualizados.
     * @return El Medium actualizado.
     */
    @Override
    public Medium actualizarMedium(Long id, ActualizarMediumDTO mediumDTO) {
        Medium medium = this.mediumRepository.findById(id);

        // --- Lógica de mover ubicación ---
        if (mediumDTO.ubicacionId() != null) {
            Ubicacion nuevaUbicacion = this.ubicacionRepository.findByIdSinConexiones(mediumDTO.ubicacionId());
            medium.setUbicacion(nuevaUbicacion);

            // efecto demo: restar cordura automáticamente
            medium.perderMana(20);
        }

        // --- Lógica de actualizar atributos existentes ---
        if (mediumDTO.manaMax() != null || mediumDTO.mana() != null) {
            medium.verificarManayManaMax(mediumDTO.manaMax(), mediumDTO.mana());
        }

        mediumDTO.sobrescribir(medium);

        return this.mediumRepository.save(medium);
    }

    /**
     * Elimina un Medium de la base de datos en base al ID recibido. Si no existe en la base de datos, no hace nada.
     * @param mediumId El ID del Medium a ser eliminado.
     */
    @Override
    public void delete(Long mediumId) {
        this.mediumRepository.deleteById(mediumId);
    }

    /**
     * Recupera todos los Mediums en la base de datos.
     * @return Una lista de Mediums recuperados.
     */
    @Override
    public List<Medium> findAll() {
        return this.mediumRepository.findAll();
    }

    /**
     * Recupera 15 puntos de mana y cada espiritu conectado a el recuperara 5 puntos de conexion.
     * @param mediumId El ID del Medium que se quiere poner a descansar.
     */
    @Override
    public void descansar(Long mediumId) {
        Medium medium = mediumRepository.findById(mediumId);
        medium.descansar();
        mediumRepository.save(medium);
    }

    /**
     * Recupera todos los espiritus de la base de datos que esten conectados al Medium recibido.
     * @param mediumId El ID del Medium del cual se quieren conocer los espiritus.
     * @return Una lista de espiritus que estan conectados al Medium, si no existen Espiritus de ese Medium, da lista vacia.
     */
    @Override
    public List<Espiritu> espiritus(Long mediumId) {
        return this.espirituRepository.findByMediumId(mediumId);
    }

    /**
     * Cambia la ubicacion del Espiritu a la del Medium siempre y cuando el Espiritu este libre y el Medium tenga suficiente mana.
     * @param mediumId El ID del Medium del cual realiza la invocacion y el espirituId al cual se quiere invocar.
     * @return Un Espiritu con su ubicacion cambiada a la ubicacion del Medium, si el Espiritu no esta libre, tira una excepcion .
     */
    @Override
    public Espiritu invocar(Long mediumId, Long espirituId) {
        Espiritu espiritu = espirituRepository.findById(espirituId);
        Medium medium = mediumRepository.findById(mediumId);
        this.validarCercaniaEspiritu(medium, espiritu);
        medium.invocarA(espiritu);
        mediumRepository.save(medium);
        return espiritu;
    }

    private void validarCercaniaEspiritu(Medium medium, Espiritu espiritu) {
        final double DISTANCIA_MAXIMA_KM = 50.0;

        GeoJsonPoint pointMedium = new GeoJsonPoint(
                medium.getCoordenadas().getLongitud(),
                medium.getCoordenadas().getLatitud()
        );
        GeoJsonPoint pointEspiritu = new GeoJsonPoint(
                espiritu.getCoordenadas().getLongitud(),
                espiritu.getCoordenadas().getLatitud()
        );

        boolean estaCerca = calculadorDeDistanciaRepository.estaDentroDelRango(
                pointMedium, pointEspiritu, DISTANCIA_MAXIMA_KM
        );

        if (!estaCerca) {
            throw new DistanciaMaximaParaInvocacion();
        }
    }

    /**
     * Un Medium exorciza a otro Medium.
     * @param idMediumExorcista  El ID del Medium que exorciza.
     * @param idMediumAExorcizar El ID del Medium a ser exorcizado.
     */
    @Override
    public void exorcizar(Long idMediumExorcista, Long idMediumAExorcizar) {
        Medium exorcista = mediumRepository.findById(idMediumExorcista);
        Medium victima = mediumRepository.findById(idMediumAExorcizar);
        List<Angel> angelesExorcista = espirituRepository.findAllAngelesByMediumId(idMediumExorcista);

        exorcista.exorcizar(angelesExorcista, victima);
        espirituRepository.saveAll(angelesExorcista);
        mediumRepository.save(victima);
    }

    @Override
    public void mover(Long mediumId, Double latitud, Double longitud) {
        Medium medium = this.findById(mediumId);
        this.cargarUbicacionCompletaDelMedium(medium);

        // busca la ubicacion que contenga esas coordenadas
        Ubicacion ubicacionDestino = ubicacionRepository.findByCoordenadas(latitud, longitud);

        medium.moverA(ubicacionDestino, latitud, longitud);

        mediumRepository.save(medium);
        espirituRepository.saveAll(medium.getEspiritus()); //Se guardan las nuevas coordenadas de todos los espiritus que movio
        this.eliminarSiNoPuedeMoverse(medium);
    }

    @Override
    public void reducirCordura(Long mediumId, Long espirituId) {
        Medium medium = mediumRepository.findById(mediumId);
        Espiritu espiritu = espirituRepository.findById(espirituId);
        medium.reducirCorduraPorHostilidadDe(espiritu);
        mediumRepository.save(medium);
    }

    private void cargarUbicacionCompletaDelMedium(Medium medium) {
        Ubicacion ubicacionCompleta = ubicacionRepository.findByIdConConexiones(medium.getUbicacion().getId());
        medium.setUbicacion(ubicacionCompleta);
    }

    private void eliminarSiNoPuedeMoverse(Medium medium) {
        if (!medium.tieneMana()) {
            List<Espiritu> espiritusLiberados = medium.desconectarTodosLosEspiritus();
            mediumRepository.save(medium);
            espirituRepository.saveAll(espiritusLiberados);
            mediumRepository.deleteById(medium.getId());
        }
    }
}
