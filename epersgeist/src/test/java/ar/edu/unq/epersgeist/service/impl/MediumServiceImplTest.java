package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.epersgeist.exception.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.TipoUbicacion;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistence.mongo.entity.CalculadorDeDistancia;
import ar.edu.unq.epersgeist.persistence.mongo.impl.UbicacionMongoDAOImpl;
import ar.edu.unq.epersgeist.persistence.mongo.interfaces.UbicacionMongoDAO;
import ar.edu.unq.epersgeist.service.interfaces.EspirituService;
import ar.edu.unq.epersgeist.service.interfaces.TestService;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MediumServiceImplTest extends ContainerIntegrationProviderTest {

    @Autowired
    private UbicacionService ubicacionService;
    @Autowired
    private EspirituService espirituService;
    @Autowired
    private MediumServiceImpl mediumService;
    @Autowired
    private TestService testService;
    @Autowired
    private UbicacionMongoDAO ubicacionMongoDAO;
    @Autowired
    private MongoTemplate mongoTemplate;

    private Medium mediumHornet;
    private Medium mediumKnight;
    private Espiritu angel;
    private Espiritu demonio;
    private Ubicacion greenpath;
    private Ubicacion dirtmouth;
    private Set<Coordenadas> coordenadasSet;
    private Set<Coordenadas> coordenadasSet2;


    // -------------- SETUP ----------------
    @BeforeEach
    void setUp() {
        mongoTemplate.indexOps(CalculadorDeDistancia.class).ensureIndex(
                new GeospatialIndex("punto").typed(GeoSpatialIndexType.GEO_2DSPHERE)
        );

        // Vertices
        coordenadasSet = Set.of(
                new Coordenadas(45.0, 45.0),
                new Coordenadas(45.2, 45.2),
                new Coordenadas(45.1, 45.1)
        );

        coordenadasSet2 = Set.of(
                new Coordenadas(45.3, 45.3),
                new Coordenadas(45.4, 45.3),
                new Coordenadas(45.3, 45.4)
        );

        greenpath = new Ubicacion("Greenpath", 50, TipoUbicacion.CEMENTERIO, coordenadasSet);
        dirtmouth = new Ubicacion("Dirtmouth", 75, TipoUbicacion.SANTUARIO, coordenadasSet2);

        ubicacionService.create(greenpath);
        ubicacionService.create(dirtmouth);

        angel = new Angel("Cornifer", dirtmouth, 20.0);
        demonio = new Demonio("Grimm", greenpath, 20.0);

        espirituService.create(angel);
        espirituService.create(demonio);

        mediumHornet = new Medium("Hornet", 420, 69, greenpath);
        mediumKnight = new Medium("The Knight", 300, 150, dirtmouth);

        mediumService.create(mediumHornet);
        mediumService.create(mediumKnight);
    }




    // --------------- CREATE ----------------


    @Test
    @DisplayName("Create: Se puede crear un medium, al cual se le asigna un ID y se persiste correctamente")
    void crear() {
        // --- Verify ---
        assertNotNull(mediumHornet.getId());
        assertEquals("Hornet", mediumHornet.getNombre());
        assertEquals("Greenpath", mediumHornet.getUbicacion().getNombre());
        assertEquals(mediumHornet.getId(), mediumService.findById(mediumHornet.getId()).getId());
        assertEquals(mediumHornet.getNombre(), mediumService.findById(mediumHornet.getId()).getNombre());
    }


   @Test
    @DisplayName("Create: No se puede crear medium con parametros null")
    void crearFallidoSinUbicacion() {
       // --- Exercise & Verify ---
        assertThrows(NullPointerException.class, () -> {
            Medium mediumSinNombre = new Medium(null, 100, 50, greenpath);
        });
        assertThrows(NullPointerException.class, () -> {
            Medium mediumSinManaMax = new Medium("Medium sin manaMax", null, 50, greenpath);
        });
        assertThrows(NullPointerException.class, () -> {
            Medium mediumSinMana = new Medium("Medium sin mana", 100, null, greenpath);
        });
        assertThrows(NullPointerException.class, () -> {
            Medium mediumSinUbicacion = new Medium("Medium sin ubicacion", 100, 50, null);
        });
    }


    @Test
    @DisplayName("Create: No se puede crear medium si su ubicacion no esta persistida")
    void crearFallidoUbicacionSinPersistencia(){
        // --- Setup ---
        Ubicacion ubiNoPersistida = new Ubicacion("Fog Canyon", 80, TipoUbicacion.CEMENTERIO, coordenadasSet);

        // --- Exercise & Verify ---
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            mediumService.create(new Medium("Quirrel", 100, 50, ubiNoPersistida));
        });

    }




    // --------------- READ ----------------


    @Test
    @DisplayName("Read: Se puede recuperar un medium por su ID")
    void findById() {
        // --- Exercise ---
        Medium mediumRecuperado = mediumService.findById(mediumHornet.getId());

        // --- Verify ---
        assertNotNull(mediumRecuperado);
        assertEquals(mediumHornet.getId(), mediumRecuperado.getId());
    }


    @Test
    @DisplayName("Read: No se puede recuperar porque medium con ese id no existe en la base de datos")
    void findByIdFallidoPorMediumSinPersistir() {
        // --- Exercise & Verify ---
        assertThrows(EntityNotFoundException.class, () -> {
            mediumService.findById(9999L);
        });
    }


    @Test
    @DisplayName("Read: Buscar un Medium con un ID nulo lanza IllegalArgumentException")
    void findByIdConIdNulo() {
        // --- Exercise & Verify ---
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            mediumService.findById(null);
        });
    }


    @Test
    @DisplayName("Read: Se ve reflejado en ambos findBy la conexion Medium y Espiritu")
    void findByIdParaAmbos() {
        // --- Setup ---
        espirituService.conectar(demonio.getId(), mediumHornet.getId());


        // --- Exercise ---
        Medium mediumRecuperado = mediumService.findById(mediumHornet.getId());
        Espiritu espirituRecuperado = espirituService.findById(demonio.getId());


        // --- Verify ---
        assertEquals(mediumHornet.getId(), mediumRecuperado.getId());
        assertEquals(demonio.getId(), espirituRecuperado.getId());
        // se fija si esta el espiritu en la lista del medium
        assertTrue(mediumRecuperado.getEspiritus().getFirst().getId().equals(espirituRecuperado.getId()));
        // se fija si el espiritu si su owner no es null y es el medium
        assertNotNull(espirituRecuperado.getMedium());
        assertEquals(mediumRecuperado.getId(), espirituRecuperado.getMedium().getId());
    }



    // --------------- UPDATE ----------------


    @Test
    @DisplayName("Update: Se puede actualizar la información de un medium existente")
    void update() {
        // --- Setup ---
        mediumHornet.setUbicacion(dirtmouth);


        // --- Exercise ---
        mediumService.update(mediumHornet);

        Medium mediumActualizado = mediumService.findById(mediumHornet.getId());


        // --- Verify ---
        assertNotNull(mediumActualizado);
        assertEquals(mediumHornet.getId(), mediumActualizado.getId());
        assertEquals(mediumHornet.getNombre(), mediumActualizado.getNombre());
    }


    @Test
    @DisplayName("Update: No se puede actualizar la información de un espíritu no persistido, se crea una copia persistente")
    void updateMediumNoPersistidoAnteriormente() {
        // --- Setup ---
        Medium mediumNoPersistido = new Medium("Quirrel", 200, 100, dirtmouth);


        // --- Exercise ---
        Medium mediumPersistido = mediumService.findById(mediumService.update(mediumNoPersistido).getId());


        // --- Verify ---
        assertNotNull(mediumPersistido);
        assertNotNull(mediumPersistido.getId());
        assertEquals(mediumNoPersistido.getNombre(), mediumPersistido.getNombre());
        assertEquals(mediumNoPersistido.getManaMax(), mediumPersistido.getManaMax());
        assertEquals(mediumNoPersistido.getMana(), mediumPersistido.getMana());
    }


    @Test
    @DisplayName("Update: Actualizar un Medium con un campo nulo lanza PropertyValueException")
    void updateConCampoNulo() {
        // --- Setup ---
        mediumHornet.setUbicacion(null);

        // --- Exercise & Verify ---
        assertThrows(NullPointerException.class, () -> {
            mediumService.update(mediumHornet);
        });
    }


    @Test
    @DisplayName("Update: Actualizar un Medium con el mismo estado que ya tiene y verifica que no hay cambios inesperados")
    void updateSinCambios() {
        // --- Setup ---
        Medium mediumAntesDeUpdate = mediumService.findById(mediumHornet.getId());


        // --- Exercise ---
        Medium mediumDespuesDeUpdate = mediumService.findById(mediumService.update(mediumHornet).getId());


        // --- Verify ---
        assertEquals(mediumAntesDeUpdate.getId(), mediumDespuesDeUpdate.getId());
        assertEquals(mediumAntesDeUpdate.getNombre(), mediumDespuesDeUpdate.getNombre());
        assertEquals(mediumAntesDeUpdate.getManaMax(), mediumDespuesDeUpdate.getManaMax());
        assertEquals(mediumAntesDeUpdate.getMana(), mediumDespuesDeUpdate.getMana());
        assertEquals(mediumAntesDeUpdate.getUbicacion().getId(), mediumDespuesDeUpdate.getUbicacion().getId());
    }




    // --------------- DELETE ----------------


    @Test
    @DisplayName("Delete: Se puede eliminar un medium existente")
    void eliminar() {
        // --- Setup ---
        mediumService.delete(mediumHornet.getId());

        // --- Exercise & Verify ---
        assertThrows(EntityNotFoundException.class, () -> {
            mediumService.findById(mediumHornet.getId());
        });
    }


   @Test
    @DisplayName("Delete: Eliminar un Medium con un ID inexistente o nulo no hace nada")
    void eliminarConIdNulo() {
       // --- Exercise & Verify ---
       assertThrows(EntityNotFoundException.class,() -> {
            mediumService.delete(9999L);
            mediumService.delete(null);
        });
    }


    // -------------- FIND ALL ----------------

    @Test
    @DisplayName("RecuperarTodos: Trae los mediums que esten en la base")
    void recuperarTodosLosMedium() {
        // --- Setup ---
        Medium mediumNuevo = new Medium("Quirrel", 200, 100, dirtmouth);
        mediumService.create(mediumNuevo);

        // --- Exercise ---
        List<Medium> mediumsRecuperados = mediumService.findAll();

        // --- Verify ---
        assertEquals(3, mediumsRecuperados.size());
    }

    @Test
    @DisplayName("RecuperarTodos: Traer medium cuando la base no tiene devuelve lista vacia")
    void recuperarTodosLosMediumListaVacia() {
        // --- Setup ---
        mediumService.delete(mediumHornet.getId());
        mediumService.delete(mediumKnight.getId());

        // --- Exercise ---
        List<Medium> mediumsRecuperados = mediumService.findAll();

        // --- Verify ---
        assertEquals(0, mediumsRecuperados.size());
    }

    // -------------- DESCANSAR ----------------

    @Test
    @DisplayName("Descansar: Recupera el mana del medium si cumple las condiciones y si no tiene espiritus no pasa nada")
    void descansarMediumSinEspiritus(){
        // --- Setup ---
        Long mediumId = mediumHornet.getId();

        // --- Exercise ---
        mediumService.descansar(mediumId);
        Medium mediumRecuperado = mediumService.findById(mediumId);

        // --- Verify ---
        assertEquals(94, mediumRecuperado.getMana());
        assertEquals(0, espirituService.espiritusDelMedium(mediumId).size());

    }

    @Test
    @DisplayName("Descansar: No recupera el mana del medium ya que tiene el max mana y si no tiene espiritus no pasa nada")
    void descansarMediumSinEspiritusYConManaMaximo(){
        // --- Setup ---
        mediumHornet.setManaMax(420);
        mediumHornet.setMana(420);
        mediumService.update(mediumHornet);
        Long mediumId = mediumHornet.getId();


        // --- Exercise ---
        mediumService.descansar(mediumId);


        // --- Verify ---
        assertEquals(420, mediumHornet.getMana());
    }

    @Test
    @DisplayName("Descansar: No recupera el mana del medium ya que tiene el max mana y le aumenta la conexion al espiritu")
    void descansarMediumConEspiritusYConManaMaximo(){
        // --- Setup ---
        Long demonioId = demonio.getId();
        Long mediumId = mediumHornet.getId();

        demonio.setNivelDeConexion(0);
        mediumHornet.setManaMax(420);
        mediumHornet.setMana(420);

        mediumService.update(mediumHornet);
        espirituService.update(demonio);

        espirituService.conectar(demonioId, mediumId);

        // Validación del estado inicial
        Espiritu demonioRecuperadoEstadoInicial = espirituService.findById(demonioId);
        assertEquals(84, demonioRecuperadoEstadoInicial.getNivelDeConexion());

        // --- Exercise ---
        mediumService.descansar(mediumId);

        // --- Verify ---
        Espiritu demonioRecuperado = espirituService.findById(demonioId);
        Medium mediumRecuperado = mediumService.findById(mediumId);

        assertEquals(100, demonioRecuperado.getNivelDeConexion());
        assertEquals(420, mediumRecuperado.getMana());
    }

    @Test
    @DisplayName("Descansar: recupera mana el medium y conexion los espiritus vinculados a el")
    void descansarMediumConEspiritus(){
        // --- Setup ---
        demonio.setNivelDeConexion(20);
        demonio.setCoordenadas(new Coordenadas(10.001, 10.001));

        Coordenadas coordenadasCercanas = new Coordenadas(10.0, 10.0);
        mediumHornet.setCoordenadas(coordenadasCercanas);

        espirituService.update(demonio);
        mediumService.update(mediumHornet);

        Long demonioId = demonio.getId();
        Long mediumId = mediumHornet.getId();

        mediumService.invocar(mediumId,demonioId);
        espirituService.conectar(demonioId,mediumId);

        // Validación del estado inicial
        Medium mediumRecuperadoEstadoInicial = mediumService.findById(mediumId);
        Espiritu demonioRecuperadoEstadoInicial = espirituService.findById(demonioId);

        assertEquals(59, mediumRecuperadoEstadoInicial.getMana()); // (69 - 10)
        assertEquals(31, demonioRecuperadoEstadoInicial.getNivelDeConexion()); // (59me * 0.2) + 20

        // --- Exercise ---
        mediumService.descansar(mediumId);

        // --- Verify ---
        Medium mediumRecuperado = mediumService.findById(mediumId);

        assertEquals(84, mediumRecuperado.getMana()); //69 - 10 + 25

        Espiritu demonioRecuperado = espirituService.findById(demonioId);

        assertEquals(81, demonioRecuperado.getNivelDeConexion()); //20 + (59 . 0,2) (conectar) + 50(descansar)
    }

    @Test
    @DisplayName("Descansar: recupera mana el medium y conexion los espiritus vinculados a el, si algun espiritu supera el max de conexion solamente recibe los puntos necesarios para no superar el max")
    void descansarMediumConUnEspirituQueLlegaAlNivelDeConexionMax() {
        // --- Setup ---
        mediumHornet.setUbicacion(dirtmouth);
        mediumHornet.setMana(40);
        angel.setNivelDeConexion(91);

        Coordenadas coordMedium = new Coordenadas(10.0, 10.0);
        Coordenadas coordAngel = new Coordenadas(10.001, 10.001);

        mediumHornet.setCoordenadas(coordMedium);
        angel.setCoordenadas(coordAngel);

        mediumService.update(mediumHornet);
        espirituService.update(angel);

        Long angelId = angel.getId();
        Long mediumId = mediumHornet.getId();

        mediumService.invocar(mediumId,angelId);
        espirituService.conectar(angelId, mediumId);

        // Validación del estado inicial
        Medium mediumRecuperadoEstadoInicial = mediumService.findById(mediumId);
        Espiritu angelRecuperadoEstadoInicial = espirituService.findById(angelId);

        assertEquals(30, mediumRecuperadoEstadoInicial.getMana()); // (40 - 10)
        assertEquals(97, angelRecuperadoEstadoInicial.getNivelDeConexion()); // (30me * 0.2) + 91

        // --- Exercise ---
        mediumService.descansar(mediumId);

        // --- Verify ---
        Medium mediumRecuperado = mediumService.findById(mediumId);
        Espiritu angelRecuperado = espirituService.findById(angelId);

        assertEquals(142, mediumRecuperado.getMana()); //40 - 10 + 112
        assertEquals(100, angelRecuperado.getNivelDeConexion());
    }

    @Test
    @DisplayName("Descansar: Un medium con un angel descansa en un cementerio y el angel no se recupera")
    void mediumDescansaConAngelEnUnCementerio() {
        // --- Setup ---
        mediumHornet.setUbicacion(dirtmouth);

        Coordenadas coordMedium = new Coordenadas(10.0, 10.0);
        Coordenadas coordAngel = new Coordenadas(10.001, 10.001);

        mediumHornet.setCoordenadas(coordMedium);
        angel.setCoordenadas(coordAngel);

        mediumService.update(mediumHornet);
        espirituService.update(angel);

        Long angelId = angel.getId();
        Long mediumId = mediumHornet.getId();

        mediumService.invocar(mediumId,angelId); // (69 - 10)
        espirituService.conectar(angelId, mediumId); //(59 * 0.2) + 0

        // Validación del estado inicial
        Medium mediumRecuperadoEstadoInicial = mediumService.findById(mediumId);
        Espiritu angelRecuperadoEstadoInicial = espirituService.findById(angelId);

        assertEquals(59, mediumRecuperadoEstadoInicial.getMana());
        assertEquals(11, angelRecuperadoEstadoInicial.getNivelDeConexion());

        //se cambia a cementerio
        mediumHornet.setUbicacion(greenpath);
        mediumService.update(mediumHornet);

        // --- Exercise ---
        mediumService.descansar(mediumId);

        // --- Verify ---
        Espiritu angelRecuperado = espirituService.findById(angelId);

        assertEquals(11, angelRecuperado.getNivelDeConexion());
    }

    @Test
    @DisplayName("Descansar: Un medium con un demonio descansa un santuario y el demonio no se recupera")
    void mediumConDemonioDescansaEnUnSantuario() {
        // --- Setup ---
        Coordenadas coordMedium = new Coordenadas(10.0, 10.0);
        Coordenadas coordDemon = new Coordenadas(10.001, 10.001);

        mediumHornet.setCoordenadas(coordMedium);
        demonio.setCoordenadas(coordDemon);

        mediumService.update(mediumHornet);
        espirituService.update(demonio);

        Long demonioId = demonio.getId();
        Long mediumId = mediumHornet.getId();

        mediumService.invocar(mediumId,demonioId);
        espirituService.conectar(demonioId, mediumId);

        // Validación del estado inicial
        Medium mediumRecuperadoEstadoInicial = mediumService.findById(mediumId);
        Espiritu demonoRecuperadoEstadoInicial = espirituService.findById(demonioId);

        assertEquals(59, mediumRecuperadoEstadoInicial.getMana());
        assertEquals(11, demonoRecuperadoEstadoInicial.getNivelDeConexion());

        //se cambia a santuario
        mediumHornet.setUbicacion(dirtmouth);
        mediumService.update(mediumHornet);

        // --- Exercise ---
        mediumService.descansar(mediumId);

        // --- Verify ---
        Espiritu demonioRecuperado = espirituService.findById(demonioId);

        assertEquals(11, demonioRecuperado.getNivelDeConexion());
    }

    @Test
    @DisplayName("Descansar: Un medium con un angel descansa en un santuario y el angel se recupera")
    void mediumDescansaConAngelEnUnSantuario() {
        // --- Setup ---
        mediumHornet.setUbicacion(dirtmouth);

        Coordenadas coordMedium = new Coordenadas(10.0, 10.0);
        Coordenadas coordAngel = new Coordenadas(10.001, 10.001);

        mediumHornet.setCoordenadas(coordMedium);
        angel.setCoordenadas(coordAngel);

        mediumService.update(mediumHornet);
        espirituService.update(angel);


        Long angelId = angel.getId();
        Long mediumId = mediumHornet.getId();

        mediumService.invocar(mediumId,angelId); //69- 10 = 59 maná
        espirituService.conectar(angelId, mediumId); //0 + (59 * 0.2) = 11

        // Validación del estado inicial
        Medium mediumRecuperadoEstadoInicial = mediumService.findById(mediumId);
        Espiritu angelRecuperadoEstadoInicial = espirituService.findById(angelId);

        assertEquals(59, mediumRecuperadoEstadoInicial.getMana());
        assertEquals(11, angelRecuperadoEstadoInicial.getNivelDeConexion());

        // --- Exercise ---
        mediumService.descansar(mediumId);//11 + 75(flujo de energia santuario)

        // --- Verify ---
        Espiritu angelRecuperado = espirituService.findById(angelId);

        assertEquals(86, angelRecuperado.getNivelDeConexion());
    }

    @Test
    @DisplayName("Descansar: Un medium con un demonio descansa un cementerio y el demonio se recupera")
    void mediumConDemonioDescansaEnUnCementerio() {
        // --- Setup ---
        Coordenadas coordMedium = new Coordenadas(10.0, 10.0);
        Coordenadas coordDemon = new Coordenadas(10.001, 10.001);

        mediumHornet.setCoordenadas(coordMedium);
        demonio.setCoordenadas(coordDemon);

        mediumService.update(mediumHornet);
        espirituService.update(demonio);

        Long demonioId = demonio.getId();
        Long mediumId = mediumHornet.getId();

        mediumService.invocar(mediumId,demonioId);
        espirituService.conectar(demonioId, mediumId);

        // Validación del estado inicial
        Medium mediumRecuperadoEstadoInicial = mediumService.findById(mediumId);
        Espiritu demonioRecuperadoEstadoInicial = espirituService.findById(demonioId);

        assertEquals(59, mediumRecuperadoEstadoInicial.getMana());
        assertEquals(11, demonioRecuperadoEstadoInicial.getNivelDeConexion());

        // --- Exercise ---
        mediumService.descansar(mediumId); //11 + 50 (flujo de energia cementerio)

        // --- Verify ---
        Espiritu demonioRecuperado = espirituService.findById(demonioId);

        assertEquals(61, demonioRecuperado.getNivelDeConexion());
    }

    // --------------- INVOCAR ----------------

    @Test
    @DisplayName("Invocar: Se intenta invocar un Angel y un Demonio en una Ubicacion prohibida y tira excepcion")
    void mediumInvocaAEspirituEnUbicacionProhibida(){
        // --- Setup ---
        Coordenadas coordBase = new Coordenadas(10.0, 10.0);
        Coordenadas coordAngel = new Coordenadas(10.002, 10.002);
        Coordenadas coordDemonio = new Coordenadas(10.001, 10.001);

        mediumHornet.setCoordenadas(coordBase);
        angel.setCoordenadas(coordAngel);

        mediumKnight.setCoordenadas(coordBase);
        demonio.setCoordenadas(coordDemonio);

        mediumService.update(mediumHornet);
        mediumService.update(mediumKnight);
        espirituService.update(angel);
        espirituService.update(demonio);

        Long mediumHornetId = mediumHornet.getId();     // greenpath cementerio
        Long angelId = angel.getId();             // dirtmouth santuario

        Long mediumKnightId = mediumKnight.getId();    // dirtmouth santuario
        Long demonioId = demonio.getId();        // greenpath cementerio


        // --- Exercise & Verify ---
        assertThrows(DiferenteUbicacionException.class, () -> mediumService.invocar(mediumHornetId, angelId));
        assertThrows(DiferenteUbicacionException.class, () ->  mediumService.invocar(mediumKnightId, demonioId));


        // --- Verify ---
        assertEquals(dirtmouth.getId(), angel.getUbicacion().getId());
        assertEquals(greenpath.getId(), demonio.getUbicacion().getId());

        assertEquals(69, mediumHornet.getMana());
        assertEquals(150, mediumKnight.getMana());
    }

    @Test
    @DisplayName("Invocar: Se intenta invocar un Angel en Cementerio y tira excepcion")
    void mediumInvocaAAngelEnCementerio(){
        // --- Setup ---
        Coordenadas coordenadasCercanas = new Coordenadas(10.0, 10.0);
        mediumHornet.setCoordenadas(coordenadasCercanas);
        mediumService.update(mediumHornet);
        angel.setCoordenadas(new Coordenadas(10.001, 10.001));
        espirituService.update(angel);

        Long mediumHornetId = mediumHornet.getId();     // greenpath cementerio
        Long angelId = angel.getId(); // dirtmouth santuario

        // --- Exercise & Verify ---
        assertThrows(DiferenteUbicacionException.class, () -> mediumService.invocar(mediumHornetId, angelId));

        // --- Verify ---
        assertEquals(dirtmouth.getId(), angel.getUbicacion().getId());
        assertEquals(69, mediumHornet.getMana());
    }

    @Test
    @DisplayName("Invocar: Se intenta invocar un Demonio en Santuario y tira excepcion")
    void mediumInvocaADemonioEnSantuario(){
        // --- Setup ---
        Coordenadas coordMedium = new Coordenadas(10.0, 10.0);
        Coordenadas coordDemon = new Coordenadas(10.001, 10.001);

        mediumKnight.setCoordenadas(coordMedium);
        demonio.setCoordenadas(coordDemon);

        mediumService.update(mediumKnight);
        espirituService.update(demonio);

        Long mediumKnightId = mediumKnight.getId();    // dirtmouth santuario
        Long demonioId = demonio.getId();        // greenpath cementerio


        // --- Exercise & Verify ---
        assertThrows(DiferenteUbicacionException.class, () ->  mediumService.invocar(mediumKnightId, demonioId));


        // --- Verify ---
        assertEquals(greenpath.getId(), demonio.getUbicacion().getId());
        assertEquals(150, mediumKnight.getMana());
    }

    @Test
    @DisplayName("Invocar: si se invoca a un espiritu no libre, tira excepcion")
    void mediumInvocaAEspirituNoLibre(){
        // --- Setup ---
        mediumHornet.setMana(40);

        Coordenadas coordMedium = new Coordenadas(10.0, 10.0);
        Coordenadas coordAngel = new Coordenadas(10.001, 10.001);

        mediumHornet.setCoordenadas(coordMedium);
        angel.setCoordenadas(coordAngel);
        angel.setMedium(mediumHornet);

        mediumService.update(mediumHornet);
        espirituService.update(angel);

        Long mediumId = mediumHornet.getId();
        Long angelId = angel.getId();

        // --- Exercise & Verify ---
        assertThrows(EspirituConectadoException.class, () -> {
            mediumService.invocar(mediumId,angelId);
        });

        // --- Verify ---
        assertEquals(40, mediumHornet.getMana());
        assertEquals(dirtmouth, angel.getUbicacion());

    }

    @Test
    @DisplayName("Invocar: si se invoca a un espiritu libre, el medium gasta mana")
    void mediumInvocaAEspirituLibre() {
        // --- Setup ---
        final double BASE_LAT = 10.0;
        final double BASE_LONG = 10.0;
        Coordenadas coordenadasDeEncuentro = new Coordenadas(BASE_LONG, BASE_LAT);

        mediumHornet.setMana(40);
        mediumHornet.setUbicacion(dirtmouth);
        mediumHornet.setCoordenadas(coordenadasDeEncuentro);
        mediumService.update(mediumHornet);

        angel.setUbicacion(greenpath);
        angel.setCoordenadas(coordenadasDeEncuentro);
        espirituService.update(angel);

        Espiritu angelPersistido = espirituService.findById(angel.getId());
        Medium mediumPersistido = mediumService.findById(mediumHornet.getId());

        Long mediumId = mediumHornet.getId();
        Long angelId = angel.getId();

        // --- Exercise ---
        Espiritu angelActualizado = mediumService.invocar(mediumId, angelId);

        // --- Verify ---
        assertEquals(dirtmouth.getId(), angelActualizado.getUbicacion().getId());
        assertTrue(angelActualizado.esLibre());

        Medium mediumDeDB = mediumService.findById(mediumId);
        assertEquals(30, mediumDeDB.getMana());

    }

    @Test
    @DisplayName("Invocar: si el medium no tiene mana no pasa nada")
    void mediumSinManaInvocaAEspirituLibre(){
        // --- Setup ---
        final double BASE_LAT = 10.0;
        final double BASE_LONG = 10.0;
        Coordenadas coordenadasDeEncuentro = new Coordenadas(BASE_LONG, BASE_LAT);

        mediumHornet.setMana(0);
        mediumHornet.setUbicacion(greenpath);
        mediumHornet.setCoordenadas(coordenadasDeEncuentro);
        mediumService.update(mediumHornet);

        demonio.setUbicacion(greenpath);
        demonio.setCoordenadas(coordenadasDeEncuentro);
        espirituService.update(demonio);

        Espiritu demonioPersistido = espirituService.findById(demonio.getId());
        Medium mediumPersistido = mediumService.findById(mediumHornet.getId());

        Long mediumId = mediumPersistido.getId();
        Long demonioId = demonioPersistido.getId();


        // --- Exercise ---
        Espiritu demonioNoInvocado = mediumService.invocar(mediumId,demonioId);
        Medium mediumFalloInvocacion = mediumService.findById(mediumId);


        // --- Verify ---
        assertEquals(0, mediumFalloInvocacion.getMana());
        assertTrue(demonioNoInvocado.esLibre());
    }

    @Test
    @DisplayName("Invocar: Si la distancia es mayor a 50km, tira DistanciaMaximaParaInvocacion")
    void mediumInvocaAEspirituLejanoTiraExcepcion() {
        // --- Setup ---

        Set<Coordenadas> coordenadasSetNuevo = Set.of(
                new Coordenadas(5.0, 5.0),
                new Coordenadas(5.0, 7.0),
                new Coordenadas(7.0, 5.0)
        );

        Ubicacion ubicacionNueva = new Ubicacion("DeepSeak", 50, TipoUbicacion.SANTUARIO, coordenadasSetNuevo);

        ubicacionService.create(ubicacionNueva);

        Coordenadas coordMedium = new Coordenadas(5.1, 6.9);
        Coordenadas coordEspiritu = new Coordenadas(5.1, 5.1);

        mediumHornet.setUbicacion(ubicacionNueva);
        angel.setUbicacion(ubicacionNueva);
        mediumHornet.setCoordenadas(coordMedium);
        angel.setCoordenadas(coordEspiritu);

        Medium updateadoM = mediumService.update(mediumHornet);
        Espiritu updateadoE = espirituService.update(angel);

        Long mediumId = mediumHornet.getId();
        Long angelId = angel.getId();

        angel.setMedium(null);
        espirituService.update(angel);

        // --- Exercise & Verify ---
        assertThrows(DistanciaMaximaParaInvocacion.class, () -> {
            mediumService.invocar(mediumId, angelId);
        });
    }

    // --------------- ESPIRITUS ----------------

    @Test
    @DisplayName("Espiritus: si no tiene espiritus el medium devuelve lista vacia")
    void mediumSinEspiritus(){
        // --- Setup ---
        Long mediumId = mediumHornet.getId();

        // --- Exercise ---
        List<Espiritu> espiritusDelMedium = mediumService.espiritus(mediumId);

        // --- Verify ---
        assertEquals(0, espiritusDelMedium.size());

    }

    @Test
    @DisplayName("Espiritus: se trae los espiritus del medium")
    void mediumConEspiritus(){
        // --- Setup ---
        Coordenadas coordMedium = new Coordenadas(10.0, 10.0);
        Coordenadas coordDemon = new Coordenadas(10.001, 10.001);

        mediumHornet.setCoordenadas(coordMedium);
        demonio.setCoordenadas(coordDemon);

        mediumService.update(mediumHornet);
        espirituService.update(demonio);

        Long demonioId = demonio.getId();
        Long mediumId = mediumHornet.getId();

        mediumService.invocar(mediumId,demonioId);
        espirituService.conectar(demonioId, mediumId);

        // Validación del estado inicial
        Medium mediumRecuperadoEstadoInicial = mediumService.findById(mediumId);
        Espiritu demonioRecuperadoEstadoInicial = espirituService.findById(demonioId);

        assertEquals(59, mediumRecuperadoEstadoInicial.getMana());
        assertEquals(11, demonioRecuperadoEstadoInicial.getNivelDeConexion());

        // --- Exercise ---
        List<Espiritu> espiritusDelMedium = mediumService.espiritus(mediumId);

        // --- Verify ---
        assertEquals(1, espiritusDelMedium.size());

    }


    // --------------- EXORCIZAR ----------------

    @Test
    @DisplayName("Exorcizar: El exorcista y la victima estan en ubicaciones diferentes, lanza excepcion")
    void exorcizarLanzaExcepcionPorDiferentesUbicaciones() {
        // --- Setup ---
        mediumKnight.conectarseAEspiritu(angel);
        mediumHornet.conectarseAEspiritu(demonio);

        mediumService.update(mediumHornet);
        mediumService.update(mediumKnight);

        // Validación del estado inicial
        assertEquals(150, mediumKnight.getMana());
        assertEquals(30, angel.getNivelDeConexion());

        // --- Exercise & Verify ---
        assertThrows(DiferenteUbicacionException.class, () -> {
            mediumService.exorcizar(mediumKnight.getId(), mediumHornet.getId());
        });
    }

    @Test
    @DisplayName("Exorcizar: Se exorciza normalmente, se elimina un demonio o el angel pierde conexion. Se actualizan correctamente la DB")
    void exorcizarNormalmente() {
        // --- Setup ---
        mediumKnight.conectarseAEspiritu(angel);
        mediumKnight.setUbicacion(greenpath);
        mediumHornet.conectarseAEspiritu(demonio);
        angel.setNivelDeConexion(60);
        mediumService.update(mediumHornet);
        mediumService.update(mediumKnight);

        // Validación del estado inicial
        assertEquals(150, mediumKnight.getMana());
        assertEquals(69, mediumHornet.getMana());
        assertEquals(60, angel.getNivelDeConexion());

        // --- Exercise ---
        mediumService.exorcizar(mediumKnight.getId(), mediumHornet.getId());

        // --- Verify ---
        Espiritu demonioExorcizado = espirituService.findById(demonio.getId());
        Espiritu angelExorcista = espirituService.findById(angel.getId());

        boolean angelGana = demonioExorcizado.getNivelDeConexion() == 0 && demonioExorcizado.esLibre();
        boolean demonioGana = angelExorcista.getNivelDeConexion() == 55;

        assertTrue( angelGana || demonioGana );
    }

    @Test
    @DisplayName("Exorcizar: Se exorciza normalmente, el demonio pierde conexión o el ángel queda libre. Se actualizan correctamente la DB")
    void exorcizarConDiferenteResultado() {
        // --- Setup ---
        mediumKnight.conectarseAEspiritu(angel);
        mediumKnight.setUbicacion(greenpath);
        mediumHornet.conectarseAEspiritu(demonio);
        angel.setNivelDeConexion(5);
        mediumService.update(mediumHornet);
        mediumService.update(mediumKnight);

        // Validación del estado inicial
        assertEquals(150, mediumKnight.getMana());
        assertEquals(69, mediumHornet.getMana());
        assertEquals(5, angel.getNivelDeConexion());

        // --- Exercise ---
        mediumService.exorcizar(mediumKnight.getId(), mediumHornet.getId());

        // --- Verify ---
        Espiritu demonioExorcizado = espirituService.findById(demonio.getId());
        Espiritu angelExorcista = espirituService.findById(angel.getId());

        boolean angelGana = demonioExorcizado.getNivelDeConexion() == 11;
        boolean demonioGana = angelExorcista.getNivelDeConexion() == 0 && angelExorcista.esLibre();

        assertTrue(angelGana || demonioGana);
    }

    @Test
    @DisplayName("Exorcizar: lanza excepción si alguno de los mediums no existe")
    void exorcizarMediumNoExiste() {
        // --- Exercise & Verify ---
        assertThrows(EntityNotFoundException.class, () -> {
            mediumService.exorcizar(9999L, mediumKnight.getId());
        });
    }


    @Test
    @DisplayName("Exorcizar: exorcista sin angeles no puede exorcizar")
    void exorcizarExorcistaSinAngeles() {
        // --- Exercise & Verify ---
        assertThrows(ExorcistaSinAtacantesException.class, () -> {
            mediumService.exorcizar(mediumHornet.getId(), mediumKnight.getId());
        });
    }


    @Test
    @DisplayName("Exorcizar: víctima sin demonios no recibe daño y no resulta en ningun cambio")
    void exorcizarVictimaSinDemonios() {
        // --- Setup ---
        mediumKnight.conectarseAEspiritu(angel);
        mediumKnight.setUbicacion(greenpath);
        mediumService.update(mediumKnight);
        Espiritu angelActualizado = espirituService.update(angel);

        // Validación del estado inicial
        assertEquals(150, mediumKnight.getMana());
        assertEquals(30, angel.getNivelDeConexion());

        // --- Exercise & Verify ---
        assertDoesNotThrow(() -> {
            mediumService.exorcizar(mediumKnight.getId(), mediumHornet.getId());
        });

        // --- Verify ---
        assertEquals(1, mediumService.espiritus(mediumKnight.getId()).size());
        assertEquals(0, mediumService.espiritus(mediumHornet.getId()).size());
        assertEquals(30, angelActualizado.getNivelDeConexion());
    }

    //---------------MOVER A--------------

    @Test
    @DisplayName("Mover: Se intenta mover a un medium inexistente")
    void moverMediumQueNoExiste(){
        // --- Exercise & Verify ---
        EntityNotFoundException excepcion = assertThrows(EntityNotFoundException.class, () -> mediumService.mover(999L, 45d, 45d)); // coordenadas greenpath
        assertEquals("No se encontró el Medium con id " + 999L, excepcion.getMessage());
    }

    @Test
    @DisplayName("Mover: Se intenta mover a un medium con ubicacion inexistente")
    void moverMediumConUnaUbicacionQueNoExiste(){
        // --- Exercise & Verify ---
        UncategorizedMongoDbException excepcion = assertThrows(UncategorizedMongoDbException.class, () ->
                mediumService.mover(mediumHornet.getId(), 999d, 999d)
        );
        assertTrue(excepcion.getMessage().contains("out of bounds"));
    }

    @Test
    @DisplayName("Mover: Se mueve a un Medium sin espiritus")
    void moverMediumSinEspiritus(){
        // --- Setup ---
        ubicacionService.conectar(greenpath.getId(), dirtmouth.getId(), 15L);

        Ubicacion greenpathConConexion = ubicacionService.findByIdConConexiones(greenpath.getId());

        mediumHornet.setUbicacion(greenpathConConexion);
        mediumHornet.setCoordenadas(new Coordenadas(45.2, 45.2)); // coordenadas de greenpath

        Medium mediumActualizado = mediumService.update(mediumHornet);

        // Validación del estado inicial
        assertEquals(greenpath.getNombre(), mediumActualizado.getUbicacion().getNombre());
        assertEquals(1, greenpathConConexion.getConexiones().size());
        assertEquals(45.2, mediumActualizado.getCoordenadas().getLatitud());

        // --- Exercise ---
        mediumService.mover(mediumHornet.getId(), 45.3, 45.3); // coordenadas de dirtmouth

        // --- Verify ---
        Medium mediumRecuperado = mediumService.findById(mediumHornet.getId());
        assertEquals(dirtmouth.getNombre(), mediumRecuperado.getUbicacion().getNombre());
        assertEquals(45.3, mediumRecuperado.getCoordenadas().getLatitud());
    }

   @Test
    @DisplayName("Mover: Se mueve a un Medium con sus espiritus a un santuario y pierde conexion demonio")
    void moverMediumConEspiritusPierdeConexionDemonio(){
        // --- Setup ---
        ubicacionService.conectar(greenpath.getId(), dirtmouth.getId(), 15L);
        Ubicacion greenpathConConexion = ubicacionService.findByIdConConexiones(greenpath.getId());

        mediumHornet.setCoordenadas(new Coordenadas(45.2, 45.2)); //coordenadas de greenpath
        mediumHornet.setUbicacion(greenpathConConexion);
        angel.setUbicacion(greenpathConConexion);

        Espiritu angelActualizado = espirituService.update(angel);
        Medium mediumActualizado = mediumService.update(mediumHornet);

        espirituService.conectar(angelActualizado.getId(), mediumActualizado.getId());
        espirituService.conectar(demonio.getId(), mediumActualizado.getId());

        // Validación del estado inicial
        Espiritu angelConectado = espirituService.findById(angel.getId());
        Espiritu demonioConectado = espirituService.findById(demonio.getId());

        assertEquals(13, angelConectado.getNivelDeConexion());
        assertEquals(13, demonioConectado.getNivelDeConexion());
        assertEquals(1, greenpathConConexion.getConexiones().size());
        assertEquals(45.2, mediumActualizado.getCoordenadas().getLatitud());

        // --- Exercise ---
        mediumService.mover(mediumHornet.getId(), 45.3, 45.3); // coordenadas dirtmounth

        // --- Verify ---
        Medium mediumRecuperado = mediumService.findById(mediumHornet.getId());
        Espiritu demonioRecuperado = espirituService.findById(demonio.getId());
        Espiritu angelRecuperado = espirituService.findById(angel.getId());

        assertEquals(dirtmouth.getNombre(), mediumRecuperado.getUbicacion().getNombre());
        assertEquals(13, angelRecuperado.getNivelDeConexion());
        assertEquals(3, demonioRecuperado.getNivelDeConexion());
        assertEquals(45.3, mediumRecuperado.getCoordenadas().getLatitud());
        assertEquals(45.3, demonioRecuperado.getCoordenadas().getLatitud());
    }

    @Test
    @DisplayName("Mover: Se mueve a un Medium con sus espiritus a un cementerio y pierde conexion angel")
    void moverMediumConEspiritusPierdeConexionAngel() {
        // --- Setup ---
        ubicacionService.conectar(dirtmouth.getId(), greenpath.getId(), 15L);
        Ubicacion dirtmouthConConexion = ubicacionService.findByIdConConexiones(dirtmouth.getId());

        mediumHornet.setUbicacion(dirtmouthConConexion);
        mediumHornet.setCoordenadas(new Coordenadas(45.3, 45.3)); // coordenadas dirtmounth
        demonio.setUbicacion(dirtmouthConConexion);

        Medium mediumActualizado = mediumService.update(mediumHornet);
        Espiritu demonioActualizado = espirituService.update(demonio);

        espirituService.conectar(demonioActualizado.getId(), mediumActualizado.getId());
        espirituService.conectar(angel.getId(), mediumActualizado.getId());

        // --- Estado inicial ---
        Espiritu angelConectado = espirituService.findById(angel.getId());
        Espiritu demonioConectado = espirituService.findById(demonio.getId());
        assertEquals(13, angelConectado.getNivelDeConexion());
        assertEquals(13, demonioConectado.getNivelDeConexion());
        assertEquals(1, dirtmouthConConexion.getConexiones().size());
        assertEquals(45.3, mediumActualizado.getCoordenadas().getLatitud());

        // --- Exercise ---
        mediumService.mover(mediumHornet.getId(), 45.2, 45.2); //coordenadas de greenpath

        // --- Verify ---
        Medium mediumRecuperado = mediumService.findById(mediumHornet.getId());
        Espiritu demonioRecuperado = espirituService.findById(demonio.getId());
        Espiritu angelRecuperado = espirituService.findById(angel.getId());

        System.out.println(mediumRecuperado.getCoordenadas().getLatitud() + " mediumRecuperado");
        assertEquals(greenpath.getNombre(), mediumRecuperado.getUbicacion().getNombre());
        assertEquals(8, angelRecuperado.getNivelDeConexion());
        assertEquals(13, demonioRecuperado.getNivelDeConexion());
        assertEquals(45.2, mediumRecuperado.getCoordenadas().getLatitud());
        assertEquals(45.2, demonioRecuperado.getCoordenadas().getLatitud());
    }


    @Test
    @DisplayName("Mover: Se mueve a un Medium con sus espiritus a un santuario y se desvincula demonio del medium")
    void moverMediumConEspiritusSeDesvinculaDemonio(){
        // --- Setup ---
        ubicacionService.conectar(greenpath.getId(), dirtmouth.getId(), 15L);
        Ubicacion greenpathConConexion = ubicacionService.findByIdConConexiones(greenpath.getId());

        mediumHornet.setCoordenadas(new Coordenadas(45.2, 45.2)); //coordenadas de greenpath
        mediumHornet.setUbicacion(greenpathConConexion);
        angel.setUbicacion(greenpath);

        Medium mediumActualizado = mediumService.update(mediumHornet);
        Espiritu angelActualizado = espirituService.update(angel);

        espirituService.conectar(angelActualizado.getId(), mediumActualizado.getId());
        espirituService.conectar(demonio.getId(), mediumActualizado.getId());

        // Validación del estado inicial
        Espiritu angelConectado = espirituService.findById(angel.getId());
        Espiritu demonioConectado = espirituService.findById(demonio.getId());
        assertEquals(13, angelConectado.getNivelDeConexion());
        assertEquals(13, demonioConectado.getNivelDeConexion());
        demonioConectado.setNivelDeConexion(9);
        Espiritu demonioActualizado = espirituService.update(demonioConectado);
        assertEquals(9, demonioActualizado.getNivelDeConexion());
        assertEquals(1, greenpathConConexion.getConexiones().size());
        assertEquals(45.2, mediumActualizado.getCoordenadas().getLatitud());

        // --- Exercise ---
        mediumService.mover(mediumHornet.getId(), 45.3, 45.3); // coordenadas dirtmounth

        // --- Verify ---
        Medium mediumRecuperado = mediumService.findById(mediumHornet.getId());
        Espiritu demonioRecuperado = espirituService.findById(demonio.getId());
        Espiritu angelRecuperado = espirituService.findById(angel.getId());

        assertEquals(dirtmouth.getNombre(), mediumRecuperado.getUbicacion().getNombre());
        assertEquals(13, angelRecuperado.getNivelDeConexion());
        assertEquals(0, demonioRecuperado.getNivelDeConexion());
        assertTrue(demonioRecuperado.esLibre());
        assertEquals(1, mediumRecuperado.getEspiritus().size());
        assertFalse(mediumRecuperado.getEspiritus().contains(demonioRecuperado));
        assertEquals(45.3, mediumRecuperado.getCoordenadas().getLatitud());
        assertEquals(45.3, demonioRecuperado.getCoordenadas().getLatitud());
    }

    @Test
    @DisplayName("Mover: Se mueve a un Medium con sus espiritus a un cementerio y se desvincula angel del medium")
    void moverMediumConEspiritusSeDesvinculaAngel(){
        // --- Setup ---
        ubicacionService.conectar(dirtmouth.getId(), greenpath.getId(), 15L);
        Ubicacion dirtmouthConConexion = ubicacionService.findByIdConConexiones(dirtmouth.getId());

        mediumHornet.setUbicacion(dirtmouthConConexion);
        mediumHornet.setCoordenadas(new Coordenadas(45.3, 45.3)); // coordenadas dirtmounth
        mediumHornet.setMana(25);
        demonio.setUbicacion(dirtmouthConConexion);
        angel.setNivelDeConexion(0);

        Medium mediumActualizado = mediumService.update(mediumHornet);
        Espiritu demonioActualizado = espirituService.update(demonio);
        Espiritu angelActualizado = espirituService.update(angel);

        espirituService.conectar(demonioActualizado.getId(), mediumActualizado.getId());
        espirituService.conectar(angelActualizado.getId(), mediumActualizado.getId());

        // Validación del estado inicial
        Espiritu angelConectado = espirituService.findById(angel.getId());
        Espiritu demonioConectado = espirituService.findById(demonio.getId());
        assertEquals(5, angelConectado.getNivelDeConexion());
        assertEquals(5, demonioConectado.getNivelDeConexion());
        assertEquals(1, dirtmouthConConexion.getConexiones().size());
        assertEquals(45.3, mediumActualizado.getCoordenadas().getLatitud());

        // --- Exercise ---
        mediumService.mover(mediumActualizado.getId(), 45.2, 45.2); //coordenadas de greenpath

        // --- Verify ---
        Medium mediumRecuperado = mediumService.findById(mediumHornet.getId());
        Espiritu demonioRecuperado = espirituService.findById(demonio.getId());
        Espiritu angelRecuperado = espirituService.findById(angel.getId());

        assertEquals(greenpath.getNombre() ,mediumRecuperado.getUbicacion().getNombre());
        assertEquals(0, angelRecuperado.getNivelDeConexion());
        assertEquals(5, demonioRecuperado.getNivelDeConexion());
        assertTrue(angelRecuperado.esLibre());
        assertEquals(1, mediumRecuperado.getEspiritus().size());
        assertFalse(mediumRecuperado.getEspiritus().contains(angelRecuperado));
        assertEquals(45.2, mediumRecuperado.getCoordenadas().getLatitud());
        assertEquals(45.2, demonioRecuperado.getCoordenadas().getLatitud());
        assertEquals(45.2, angelRecuperado.getCoordenadas().getLatitud());
    }

    @Test
    @DisplayName("Mover: Se mueve a un Medium con sus espiritus a una ubicacion conectada")
    void moverMediumConEspiritusAUnaUbicacionConectada(){
        // --- Setup ---
        ubicacionService.conectar(dirtmouth.getId(), greenpath.getId(), 15L);
        Ubicacion dirtmouthConConexion = ubicacionService.findByIdConConexiones(dirtmouth.getId());

        mediumHornet.setUbicacion(dirtmouthConConexion);
        mediumHornet.setCoordenadas(new Coordenadas(45.3, 45.3)); // coordenadas dirtmounth
        demonio.setUbicacion(dirtmouthConConexion);

        Medium mediumActualizado = mediumService.update(mediumHornet);
        Espiritu demonioActualizado = espirituService.update(demonio);

        espirituService.conectar(demonioActualizado.getId(), mediumActualizado.getId());
        espirituService.conectar(angel.getId(), mediumActualizado.getId());

        // Validación del estado inicial
        Espiritu angelConectado = espirituService.findById(angel.getId());
        Espiritu demonioConectado = espirituService.findById(demonio.getId());
        assertEquals(13, angelConectado.getNivelDeConexion());
        assertEquals(13, demonioConectado.getNivelDeConexion());
        assertEquals(1, dirtmouthConConexion.getConexiones().size());
        assertEquals(45.3, mediumActualizado.getCoordenadas().getLatitud());

        // --- Exercise ---
        mediumService.mover(mediumActualizado.getId(), 45.2, 45.2); //coordenadas de greenpath

        // --- Verify ---
        Medium mediumRecuperado = mediumService.findById(mediumHornet.getId());
        Espiritu demonioRecuperado = espirituService.findById(demonio.getId());
        Espiritu angelRecuperado = espirituService.findById(angel.getId());

        assertEquals(greenpath.getNombre() ,mediumRecuperado.getUbicacion().getNombre());
        assertEquals(8, angelRecuperado.getNivelDeConexion());
        assertEquals(13, demonioRecuperado.getNivelDeConexion());
        assertEquals(2, mediumRecuperado.getEspiritus().size());
        assertEquals(45.2, mediumRecuperado.getCoordenadas().getLatitud());
        assertEquals(45.2, demonioRecuperado.getCoordenadas().getLatitud());
        assertEquals(45.2, angelRecuperado.getCoordenadas().getLatitud());
    }

    @Test
    @DisplayName("Mover: Se mueve a un Medium y muere por no tener mana")
    void moverMediumAUbicacionConectadaMuerePorQuedarseSinMana(){
        // --- Setup ---
        ubicacionService.conectar(dirtmouth.getId(), greenpath.getId(), 15L);
        Ubicacion dirtmouthConConexion = ubicacionService.findByIdConConexiones(dirtmouth.getId());

        mediumHornet.setMana(0);
        mediumHornet.setUbicacion(dirtmouthConConexion);
        mediumHornet.setCoordenadas(new Coordenadas(45.3, 45.3)); // coordenadas dirtmounth

        Medium mediumActualizado = mediumService.update(mediumHornet);

        // Validación del estado inicial
        assertEquals(1, dirtmouthConConexion.getConexiones().size());
        assertEquals(45.3, mediumActualizado.getCoordenadas().getLatitud());

        // --- Exercise ---
        mediumService.mover(mediumActualizado.getId(), 45.2, 45.2); //coordenadas de greenpath

        // --- Verify ---
        assertEquals(1 ,mediumService.findAll().size()); // esta el mediumKnight persistido

    }

    @Test
    @DisplayName("Mover: Se mueve a un Medium y muere por no tener mana y su espiritu queda libre")
    void moverMediumAUbicacionConectadaMuerePorQuedarseSinManaYSuEspirituQuedaLibre(){
        // --- Setup ---
        ubicacionService.conectar(dirtmouth.getId(), greenpath.getId(), 15L);
        Ubicacion dirtmouthConConexion = ubicacionService.findByIdConConexiones(dirtmouth.getId());

        mediumHornet.setMana(0);
        mediumHornet.setUbicacion(dirtmouthConConexion);
        mediumHornet.setCoordenadas(new Coordenadas(45.3, 45.3)); // coordenadas dirtmounth

        Medium mediumActualizado = mediumService.update(mediumHornet);

        espirituService.conectar(angel.getId(), mediumActualizado.getId());

        Medium mediumConectado = mediumService.findById(mediumActualizado.getId());

        // Validación del estado inicial
        Espiritu angelConectado = espirituService.findById(angel.getId());
        assertEquals(1, dirtmouthConConexion.getConexiones().size());
        assertEquals(45.3, mediumActualizado.getCoordenadas().getLatitud());
        assertFalse(angelConectado.esLibre());

        // --- Exercise ---
        mediumService.mover(mediumActualizado.getId(), 45.2, 45.2); //coordenadas de greenpath

        // --- Verify ---
        Espiritu angelRecuperado = espirituService.findById(angel.getId());
        assertEquals(1 ,mediumService.findAll().size()); // esta el mediumKnight persistido
        assertTrue(angelRecuperado.esLibre());// el medium murio
    }

    @Test
    @DisplayName("Mover: Se intenta mover a una ubicacion la cual no esta conectada con la ubicacion en la que se encuentra el medium actualmente")
    void moverMediumAUbicacionNoConectada(){
        // --- Setup ---

        mediumHornet.setUbicacion(dirtmouth);

        Medium mediumActualizado = mediumService.update(mediumHornet);

        // --- Exercise & Verify ---
        assertThrows(
                UbicacionLejanaException.class,
                () -> mediumService.mover(mediumActualizado.getId(), 45.2, 45.2) //coordenadas de greenpath
        );
    }

    @Test
    @DisplayName("Mover: Se intenta mover a una ubicacion la cual esta a mas de 30km de la ubicacion actual")
    void moverMediumAUbicacionConMasDe30Km(){
        // --- Setup ---

        ubicacionService.conectar(dirtmouth.getId(), greenpath.getId(), 15L);

        Ubicacion dirtmouthConConexion = ubicacionService.findByIdConConexiones(dirtmouth.getId());

        mediumHornet.setUbicacion(dirtmouthConConexion);
        mediumHornet.setCoordenadas(new Coordenadas(45.3, 45.3)); // coordenadas dirtmounth

        Medium mediumActualizado = mediumService.update(mediumHornet);

        // --- Exercise & Verify ---
        assertThrows(
                UbicacionLejanaException.class,
                () -> mediumService.mover(mediumActualizado.getId(), 45.0, 45.0) //coordenadas de greenpath
        );
    }

    // -------------- TEARDOWN ----------------


    @AfterEach
    public void tearDown() {
        testService.clearAll();
    }

    @AfterEach
    public void cleanupMongo() {
        mongoTemplate.remove(new Query(), CalculadorDeDistancia.class);
    }

}