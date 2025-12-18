package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.epersgeist.exception.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.TipoUbicacion;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.service.interfaces.EspirituService;
import ar.edu.unq.epersgeist.service.interfaces.MediumService;
import ar.edu.unq.epersgeist.service.interfaces.TestService;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class UbicacionServiceImplTest extends ContainerIntegrationProviderTest{

    @Autowired
    private UbicacionService ubicacionService;
    @Autowired
    private EspirituService espirituService;
    @Autowired
    private MediumService mediumService;
    @Autowired
    private TestService testService;

    private Ubicacion ubicacion;
    private Ubicacion ubicacion2;
    private Ubicacion ubicacion3;
    private Angel batista;
    private Demonio damian;
    private Medium mickey;
    private Set<Coordenadas> coordenadasSet;
    private Set<Coordenadas> coordenadasSet2;
    private Set<Coordenadas> coordenadasSet3;
    private Set<Coordenadas> coordenadasSet4;
    private Set<Coordenadas> coordenadasSet5;
    private Set<Coordenadas> coordenadasSet6;

    @BeforeEach
    void setUp() {

        coordenadasSet = Set.of(new Coordenadas(0.0, 0.0), new Coordenadas(0.0, 1.0), new Coordenadas(1.0, 0.0));
        coordenadasSet2 = Set.of(new Coordenadas(10.0, 10.0), new Coordenadas(10.0, 11.0), new Coordenadas(11.0, 10.0));
        coordenadasSet3 = Set.of(new Coordenadas(20.0, 20.0), new Coordenadas(20.0, 21.0), new Coordenadas(21.0, 20.0));
        coordenadasSet4 = Set.of(new Coordenadas(30.0, 30.0), new Coordenadas(30.0, 31.0), new Coordenadas(31.0, 30.0));
        coordenadasSet5 = Set.of(new Coordenadas(40.0, 40.0), new Coordenadas(40.0, 41.0), new Coordenadas(41.0, 40.0));
        coordenadasSet6 = Set.of(new Coordenadas(50.0, 50.0), new Coordenadas(50.0, 51.0), new Coordenadas(51.0, 50.0));

        ubicacion = new Ubicacion("Hollow Bastion", 10, TipoUbicacion.CEMENTERIO, coordenadasSet);
        ubicacion2 = new Ubicacion("Twilight Town", 20, TipoUbicacion.SANTUARIO, coordenadasSet2);
        ubicacion3 = new Ubicacion("Disneyland", 30, TipoUbicacion.CEMENTERIO, coordenadasSet3);

        ubicacionService.create(ubicacion);
        ubicacionService.create(ubicacion2);
        ubicacionService.create(ubicacion3);

        mickey = new Medium("Mickey", 100, 100, ubicacion);
        mediumService.create(mickey);

        batista = new Angel("Batista", ubicacion, 20.0);
        damian = new Demonio("Damian", ubicacion2, 20.0);
        espirituService.create(batista);
        espirituService.create(damian);

    }


    // ----------- CREATE -------------

    @Test
    @DisplayName("Create: Se puede crear una ubicación")
    void crearUbicacion() {
        // --- Verify ---
        assertNotNull(ubicacion.getId());
    }

    @Test
    @DisplayName("Create: Existe un espiritu en la ubicacion dada")
    void existeEspirituEnLaUbicacion() {
        // --- Verify ---
        assertEquals(damian.getUbicacion().getId(), ubicacion2.getId());
    }

    @Test
    @DisplayName("Create: No existe un espiritu en la ubicacion dada")
    void noExisteEspirituEnLaUbicacion() {
        // --- Verify ---
        assertNotEquals(damian.getUbicacion().getId(), ubicacion.getId());
    }

    @Test
    @DisplayName("Create: Existe un medium en la ubicacion dada")
    void existeMediumEnLaUbicacion() {
        // --- Verify ---
        assertEquals(mickey.getUbicacion().getId(), ubicacion.getId());

    }

    @Test
    @DisplayName("Create: No existe un medium en la ubicacion dada")
    void noExisteMediumEnLaUbicacion() {
        // --- Verify ---
        assertNotEquals(mickey.getUbicacion().getId(), ubicacion2.getId());
    }

    @Test
    @DisplayName("Create: No se puede crear una ubicación con nombre repetido")
    void noSePuedeCrearUbicacionConNombreRepetido() {
        // --- Setup ---
        Ubicacion ubicacionRepetida = new Ubicacion("Hollow Bastion", 30, TipoUbicacion.CEMENTERIO, coordenadasSet5);

        // --- Exercise & Verify ---
        assertThrows(NombreRepetidoException.class, () -> { ubicacionService.create(ubicacionRepetida); });
    }

    @Test
    @DisplayName("Create: No se puede crear una ubicación que solape con otra ya existente")
    void noSePuedeCrearUbicacionQueSolapeConOtraExistente() {
        // --- Setup ---
        Ubicacion ubicacionSolapada = new Ubicacion("Solapada", 30, TipoUbicacion.CEMENTERIO, coordenadasSet);

        // --- Exercise & Verify ---
        assertThrows(UbicacionSolapadaException.class, () -> { ubicacionService.create(ubicacionSolapada); });
    }

    @Test
    @DisplayName("Create: No se puede crear una ubicación con flujo < 0")
    void noSePuedeCrearUbicacionConFlujoNegativo() {
        // --- Exercise & Verify ---
        assertThrows(FlujoFueraDeRangoException.class,
                () -> { ubicacionService.create(new Ubicacion("Toy Box", -10, TipoUbicacion.CEMENTERIO, coordenadasSet6));
                });
    }

    // ----------- READ -------------

    @Test
    @DisplayName("Read: Se puede encontrar una ubicación por ID.")
    void encontrarUbicacionPorID() {
        // --- Exercise ---
        Ubicacion ubiEncontrada = ubicacionService.findByIdSinConexion(ubicacion.getId());

        // --- Verify ---
        assertNotNull(ubiEncontrada);
        assertEquals(ubicacion.getId(), ubiEncontrada.getId());
    }

    @Test
    @DisplayName("Read: Se puede encontrar una ubicación con sus conexiones por ID.")
    void encontrarUbicacionConConexionesPorID() {
        // --- Setup ---
        Map<Long, Long> conexiones = new HashMap<>();
        conexiones.put(ubicacion2.getId(), 1L);
        ubicacion.setConexiones(conexiones);
        ubicacionService.update(ubicacion);

        // --- Exercise ---
        Ubicacion ubiEncontrada = ubicacionService.findByIdConConexiones(ubicacion.getId());

        // --- Verify ---
        assertNotNull(ubiEncontrada);
        assertEquals(1, ubiEncontrada.getConexiones().size());
    }

    @Test
    @DisplayName("Read: No se puede encontrar la ubicación con el ID dado.")
    void noSeEncuentraLaUbicacionYTiraExcepcion() {
        // --- Verify ---
        assertThrows(EntityNotFoundException.class, () -> {
            ubicacionService.findByIdSinConexion(999L);
        });
    }

    @Test
    @DisplayName("Read: No se puede encontrar la ubicación con conexiones con el ID dado.")
    void noSeEncuentraLaUbicacionConConexionesYTiraExcepcion() {
        // --- Verify ---
        assertThrows(EntityNotFoundException.class, () -> {
            ubicacionService.findByIdConConexiones(999L);
        });
    }


    // ----------- UPDATE -------------

    @Test
    @DisplayName("Update: Se pueden actualizar ubicaciones")
    void actualizarUbicacion() {
        // --- Exercise ---
        ubicacion.setNombre("New Twilight Town");

        Ubicacion ubiActualizada = ubicacionService.update(ubicacion);

        // --- Verify ---
        assertNotNull(ubiActualizada);
        assertEquals("New Twilight Town", ubiActualizada.getNombre());

    }

    @Test
    @DisplayName("Update: Se pueden actualizar ubicaciones con conexiones")
    void actualizarUbicacionConConexiones() {
        // --- Setup ---
        Map<Long, Long> conexiones = new HashMap<>();
        conexiones.put(ubicacion2.getId(), 1L);
        ubicacion.setConexiones(conexiones);

        // --- Exercise ---
        Ubicacion ubiActualizada = ubicacionService.update(ubicacion);

        // --- Verify ---
        assertNotNull(ubiActualizada);
        assertEquals(1, ubiActualizada.getConexiones().size());

    }

    // ---------- DELETE -------------

    @Test
    @DisplayName("Delete: Se puede eliminar ubicaciones")
    void eliminarUbicacion() {
        // --- Setup ---
        Ubicacion ubiCreada = ubicacionService.create(new Ubicacion("Traverse Town", 50, TipoUbicacion.CEMENTERIO, coordenadasSet6));

        // --- Exercise ---
        ubicacionService.delete(ubiCreada.getId());

        // --- Verify ---
        assertThrows(EntityNotFoundException.class, () -> {
            ubicacionService.findByIdSinConexion(ubiCreada.getId());
        });
    }

    @Test
    @DisplayName("Delete: Se puede eliminar ubicaciones con conexiones")
    void eliminarUbicacionConConexiones() {
        // --- Setup ---
        Ubicacion ubiCreada = ubicacionService.create(new Ubicacion("Yharnam", 50, TipoUbicacion.CEMENTERIO, coordenadasSet5));
        Map<Long, Long> conexiones = new HashMap<>();
        conexiones.put(ubicacion2.getId(), 1L);
        ubiCreada.setConexiones(conexiones);
        ubicacionService.update(ubiCreada);
        Ubicacion ubicacionActualizada = ubicacionService.findByIdConConexiones(ubiCreada.getId());

        // --- Exercise ---
        ubicacionService.delete(ubicacionActualizada.getId());

        // --- Verify ---
        assertThrows(EntityNotFoundException.class, () -> {
            ubicacionService.findByIdSinConexion(ubicacionActualizada.getId());
        });
    }

    // ------------ CONECTAR ---------------

    @Test
    @DisplayName("conectar: Se conectan dos ubicaciones de manera unidireccional y no es bidireccional por default")
    void conectarUnidireccionalmenteDosUbicaciones() {
        // --- Exercise ---
        ubicacionService.conectar(ubicacion.getId(), ubicacion2.getId(), 40L);

        Ubicacion origen = ubicacionService.findByIdConConexiones(ubicacion.getId());
        Ubicacion destino = ubicacionService.findByIdConConexiones(ubicacion2.getId());

        // --- Verify ---
        assertEquals(40L, origen.getConexiones().get(destino.getId()));
        assertFalse(destino.getConexiones().containsKey(origen.getId()));
    }

    @Test
    @DisplayName("conectar: se realiza una nueva conexion sobre una ya creada y solamente actualiza el costo.")
    void conectarUnaConexionYaConectadaActualizaLaConexion() {
        // --- SetUp ---
        ubicacionService.conectar(ubicacion.getId(), ubicacion2.getId(), 15L);

        // --- Verificacion adicional ---
        Ubicacion origen = ubicacionService.findByIdConConexiones(ubicacion.getId());
        assertEquals(15L, origen.getConexiones().get(ubicacion2.getId()));

        // --- Exercise ---
        ubicacionService.conectar(ubicacion.getId(), ubicacion2.getId(), 30L);
        Ubicacion origenActualizado = ubicacionService.findByIdConConexiones(ubicacion.getId());

        // --- Verify ---
        assertEquals(30L, origenActualizado.getConexiones().get(ubicacion2.getId()));
        assertEquals(1, origenActualizado.getConexiones().size());
    }

    @Test
    @DisplayName("conectar: se realiza una conexion bidireccional solamente si es explicitamente hecha.")
    void conectarDosUbicacionesDeManeraBidireccional() {
        // --- Exercise ---
        ubicacionService.conectar(ubicacion.getId(), ubicacion2.getId(), 15L);
        ubicacionService.conectar(ubicacion2.getId(), ubicacion.getId(), 25L);

        Ubicacion a = ubicacionService.findByIdConConexiones(ubicacion.getId());
        Ubicacion b = ubicacionService.findByIdConConexiones(ubicacion2.getId());

        // --- Verify ---
        assertEquals(15L, a.getConexiones().get(b.getId()));
        assertEquals(25L, b.getConexiones().get(a.getId()));
    }

    @Test
    @DisplayName("conectar: si el origen es inexistente no es posible realizar la conexion.")
    void conectarConOrigenInexistenteNoEsPosible() {
        // --- Verify ---
        assertThrows(EntityNotFoundException.class,
                () -> ubicacionService.conectar(37562L, ubicacion2.getId(), 6L));
    }

    @Test
    @DisplayName("conectar: si el destino es inexistente no es posible realizar la conexion.")
    void conectarConDestinoInexistenteNoEsPosible() {
        // --- Verify ---
        assertThrows(EntityNotFoundException.class,
                () -> ubicacionService.conectar(ubicacion.getId(), 812642734L, 6L));
    }

    @Test
    @DisplayName("conectar: no es posible realizar conexiones circulares")
    void conectarSobreLaMismaUbicacionNoEsPosible() {
        // --- Verify ---
        assertThrows(RelacionCircularInvalida.class,
                () -> ubicacionService.conectar(ubicacion.getId(), ubicacion.getId(), 20L));
    }

    @Test
    @DisplayName("conectar: no es posible realizar una conexion con un costo fuera del rango [0..100]")
    void conectarConCostoFueraDeRangoNoEsPosible() {
        // --- Verify ---
        assertThrows(CostoFueraDeRango.class,
                () -> ubicacionService.conectar(ubicacion.getId(), ubicacion2.getId(), -20L));

        assertThrows(CostoFueraDeRango.class,
                () -> ubicacionService.conectar(ubicacion.getId(), ubicacion2.getId(), 101L));
    }

    // ----------- ESTAN CONECTADAS --------

    @Test
    @DisplayName("estanConectadas: retorna true si hay conexion a un paso de distancia.")
    void lasUbicacionesEstanConectadasAUnPasoDeDistancia() {
        // --- Set up y Exercise---
        ubicacionService.conectar(ubicacion.getId(), ubicacion2.getId(), 40L);

        // --- Verify ---
        assertTrue(ubicacionService.estanConectadas(ubicacion.getId(), ubicacion2.getId()));
        assertFalse(ubicacionService.estanConectadas(ubicacion2.getId(), ubicacion.getId()));
    }

    @Test
    @DisplayName("estanConectadas: retorna false porque las ubicaciones se encuentran conectadas a mas de un paso.")
    void lasUbicacionesNoSeEncuentranConectadasAUnPasoDeDistancia() {
        // --- Set up ---
        Ubicacion ubicacion3 = new Ubicacion("Cache", 80, TipoUbicacion.CEMENTERIO, coordenadasSet6);
        ubicacionService.create(ubicacion3);

        // --- Exercise ---
        ubicacionService.conectar(ubicacion.getId(), ubicacion2.getId(), 30L);
        ubicacionService.conectar(ubicacion2.getId(), ubicacion3.getId(), 30L);

        // --- Verify ---
        assertFalse(ubicacionService.estanConectadas(ubicacion.getId(), ubicacion3.getId()));
    }

    @Test
    @DisplayName("estanConectadas: Devuelve false si alguna de las ubicaciones no existe.")
    void lanzaFalseSiUnaUbicacionNoExiste() {
        // --- Exercise y Verify ---
        assertFalse(ubicacionService.estanConectadas(ubicacion.getId(), 345345L));

        assertFalse(ubicacionService.estanConectadas(7781L, ubicacion2.getId()));
    }


    // ----------- CAMINOMASCORTO -------------

    @Test
    @DisplayName("caminoMasCorto: Se puede obtener el camino más corto entre dos ubicaciones")
    void caminoMasCortoConectadas() {
        // --- Setup ---
        ubicacionService.conectar(ubicacion.getId(), ubicacion2.getId(), 10L);
        ubicacionService.conectar(ubicacion2.getId(), ubicacion3.getId(), 10L);
        ubicacionService.conectar(ubicacion.getId(), ubicacion3.getId(), 30L);

        // --- Exercise ---
        List<Ubicacion> caminoMasCorto = ubicacionService.caminoMasCorto(ubicacion.getId(), ubicacion3.getId());

        // --- Verify ---
        assertEquals(2, caminoMasCorto.size());
        assertEquals(ubicacion.getId(), caminoMasCorto.get(0).getId());
        assertEquals(ubicacion3.getId(), caminoMasCorto.get(1).getId());
    }

    @Test
    @DisplayName("caminoMasCorto: No se puede obtener el camino más corto entre ubicaciones no conectadas y lanza excepción")
    void caminoMasCortoNoConectadasLanzaExcepcion() {
        // --- Verify ---
        assertThrows(Exception.class,
                () -> ubicacionService.caminoMasCorto(ubicacion.getId(), ubicacion2.getId()));
    }

    // --------------- UBICACIONES SOBRECARGADAS ---------------

    @Test
    @DisplayName("ubicacionSobrecargada: devuelve una ubicacion que tiene energia que excede del umbral")
    void ubicacionSobrecargada() {
        // --- Exercise ---
        List<Ubicacion> ubicacionesSobrecargadas = ubicacionService.ubicacionesSobrecargadas(10);

        // --- Verify ---
        assertEquals(2, ubicacionesSobrecargadas.size());
        assertEquals(ubicacion2.getId(), ubicacionesSobrecargadas.getFirst().getId());
        assertEquals(ubicacion3.getId(), ubicacionesSobrecargadas.get(1).getId());
        assertTrue(ubicacionesSobrecargadas.getFirst().getFlujoEnergia() > 10);
        assertTrue(ubicacionesSobrecargadas.get(1).getFlujoEnergia() > 10);
    }

    @Test
    @DisplayName("ubicacionSobrecargada: no devuelve nada porque no hay ubicaciones que excedan el umbral")
    void ubicacionSobrecargadaNoDevuelveNada() {
        // --- Exercise & Verify ---
        assertEquals(0,ubicacionService.ubicacionesSobrecargadas(30).size());
        assertTrue(ubicacionService.ubicacionesSobrecargadas(30).isEmpty());
    }

    // ----------- ADICIONALES -------------

    @Test
    @DisplayName("espiritusEn: Espiritus en ubicación devuelve lista vacia si la ubicación es inválida")
    void espiritusEnUbicacionInexistente() {
        // --- Exercise ---
        List<Espiritu> espiritus = ubicacionService.espiritusEn(9999999L);

        // --- Verify ---
        assertEquals(0, espiritus.size());
    }

    @Test
    @DisplayName("espiritusEn: Espiritus en ubicacion existente retorna lista con los espíritus")
    void espiritusEnUbicacionExistente() {
        // --- Exercise ---
        List<Espiritu> espiritus = ubicacionService.espiritusEn(ubicacion.getId());

        // --- Verify ---
        assertEquals(1, espiritus.size());
    }

    @Test
    @DisplayName("mediumsSinEspiritusEn: Mediums sin espiritus en ubicacion existente retorna una lista de mediums")
    void mediumsSinEspiritusEnUbicacionExistente() {
        // --- Setup ---
        //medium con espiritus (no lo deberia tener en cuenta)
        Medium mediumConEspiritu = new Medium("Vivi", 80, 20, ubicacion);

        mediumService.create(mediumConEspiritu);
        batista.setMedium(mediumConEspiritu);
        mediumConEspiritu.getEspiritus().add(batista);
        mediumService.update(mediumConEspiritu);

        // Validación del estado inicial
        Medium mediumRecuperadoEstadoInicial = mediumService.findById(mediumConEspiritu.getId());
        assertEquals(1, mediumRecuperadoEstadoInicial.getEspiritus().size());

        // --- Exercise ---
        List<Medium> mediumsSinEspiritus = ubicacionService.mediumsSinEspiritusEn(ubicacion.getId());

        // --- Verify ---
        assertEquals(1, mediumsSinEspiritus.size());
        assertEquals("Mickey", mediumsSinEspiritus.getFirst().getNombre());

    }

    @Test
    @DisplayName("mediumsSinEspiritusEn: Mediums sin espíritus en ubicación sin mediums devuelve lista vacia")
    void mediumsSinEspiritusEnUbicacionSinMediums() {
        // --- Exercise ---
        List<Medium> mediumsSinEspiritus = ubicacionService.mediumsSinEspiritusEn(ubicacion2.getId());

        // --- Verify ---
        assertEquals(0, mediumsSinEspiritus.size());
    }

    @Test
    @DisplayName("recuperarTodos: Recuperar todas las ubicaciones")
    void recuperarTodos() {

        // --- Exercise & Verify ---
        assertTrue(ubicacionService.recuperarTodos().size() >= 2);
    }





    // ----------- CAMINO MAS RENTABLE -------------

    @Test
    @DisplayName("caminoMasRentable: Devuelve el camino más rentable entre dos ubicaciones")
    void caminoMasRentableConectadas() {
        // --- Setup ---
        Ubicacion zootopia = new Ubicacion("Zootopia", 15, TipoUbicacion.SANTUARIO, coordenadasSet4);
        Ubicacion casaMickey = new Ubicacion("La casa de MickeyMouse", 5, TipoUbicacion.CEMENTERIO, coordenadasSet5);
        Ubicacion atlantis = new Ubicacion("Atlantis", 25, TipoUbicacion.SANTUARIO, coordenadasSet6);

        ubicacionService.create(zootopia);
        ubicacionService.create(casaMickey);
        ubicacionService.create(atlantis);

        ubicacionService.conectar(ubicacion.getId(), zootopia.getId(), 50L);
        ubicacionService.conectar(zootopia.getId(), ubicacion2.getId(), 50L);   // ubicacion -50-> zootopia -50-> ubicacion2
        ubicacionService.conectar(ubicacion.getId(), casaMickey.getId(), 10L);

        ubicacionService.conectar(casaMickey.getId(), atlantis.getId(), 10L);
        ubicacionService.conectar(atlantis.getId(), ubicacion2.getId(), 10L);   // ubicacion -10-> casaMickey -10-> atlantis -10-> ubicacion2
        
        // --- Exercise ---
        List<Ubicacion> camino = ubicacionService.caminoMasRentable(ubicacion.getId(), ubicacion2.getId());

        // --- Verify ---
        assertEquals(4, camino.size());
        assertEquals(ubicacion.getId(), camino.get(0).getId());
        assertEquals(casaMickey.getId(), camino.get(1).getId());
        assertEquals(atlantis.getId(), camino.get(2).getId());
        assertEquals(ubicacion2.getId(), camino.get(3).getId());
    
    }


    @Test
    @DisplayName("caminoMasRentable: El camino más rentable entre dos ubicaciones se desvia para mejor rendimiento")
    void caminoMasRentableDesvio() {
        // --- Setup ---
        Ubicacion zootopia = new Ubicacion("Zootopia", 15, TipoUbicacion.SANTUARIO, coordenadasSet4);
        Ubicacion casaMickey = new Ubicacion("La casa de MickeyMouse", 5, TipoUbicacion.CEMENTERIO, coordenadasSet5);
        
        ubicacionService.create(zootopia);
        ubicacionService.create(casaMickey);

        ubicacionService.conectar(ubicacion.getId(), zootopia.getId(), 50L);
        ubicacionService.conectar(zootopia.getId(), ubicacion2.getId(), 50L);   // ubicacion -50-> zootopia -50-> ubicacion2
        
        ubicacionService.conectar(ubicacion.getId(), casaMickey.getId(), 10L);
        ubicacionService.conectar(casaMickey.getId(), zootopia.getId(), 10L);   // ubicacion -10-> casaMickey -10-> zootopia -50-> ubicacion2
        
        // --- Exercise ---
        List<Ubicacion> camino = ubicacionService.caminoMasRentable(ubicacion.getId(), ubicacion2.getId());

        // --- Verify ---
        assertEquals(4, camino.size());
        assertEquals(ubicacion.getId(), camino.get(0).getId());
        assertEquals(casaMickey.getId(), camino.get(1).getId());
        assertEquals(zootopia.getId(), camino.get(2).getId());
        assertEquals(ubicacion2.getId(), camino.get(3).getId());

    }


    @Test
    @DisplayName("caminoMasRentable: No existe camino entre las dos ubicaciones, esto lanza la excepción UbicacionesNoConectadas")
    void caminoMasRentableNoConectadas() {
        // --- Exercise & Verify ---
        assertThrows(UbicacionesNoConectadasException.class,
                () -> ubicacionService.caminoMasRentable(ubicacion.getId(), ubicacion2.getId()));
    }


    @Test
    @DisplayName("caminoMasRentable: Las ubicaciones dadas contienen las conexiones del camino mas rentable")
    void caminoMasRentableDevuelveConexiones() {
        // --- Setup ---
        Ubicacion zootopia = new Ubicacion("Zootopia", 15, TipoUbicacion.SANTUARIO, coordenadasSet5);
        Ubicacion casaMickey = new Ubicacion("La casa de MickeyMouse", 5, TipoUbicacion.CEMENTERIO, coordenadasSet6);

        ubicacionService.create(zootopia);
        ubicacionService.create(casaMickey);

        ubicacionService.conectar(ubicacion.getId(), zootopia.getId(), 50L);
        ubicacionService.conectar(zootopia.getId(), ubicacion2.getId(), 50L);   // ubicacion -50-> zootopia -50-> ubicacion2

        ubicacionService.conectar(ubicacion.getId(), casaMickey.getId(), 10L);
        ubicacionService.conectar(casaMickey.getId(), zootopia.getId(), 10L);   // ubicacion -10-> casaMickey -10-> zootopia -50-> ubicacion2

        // --- Exercise ---
        List<Ubicacion> camino = ubicacionService.caminoMasRentable(ubicacion.getId(), ubicacion2.getId());

        // --- Verify ---
        Ubicacion origen = camino.get(0);
        Ubicacion intermedio1 = camino.get(1);
        Ubicacion intermedio2 = camino.get(2);
        Ubicacion destino = camino.get(3);

        assertNull(origen.getConexiones().get(intermedio2.getId()));// (no es parte del camino) ubicacion  -50-> zootopia
        assertEquals(10L, origen.getConexiones().get(intermedio1.getId()));         // ubicacion  -10-> casaMickey
        assertEquals(10L, intermedio1.getConexiones().get(intermedio2.getId()));    // casaMickey -10-> zootopia
        assertEquals(50L, intermedio2.getConexiones().get(destino.getId()));        // zootopia   -50-> ubicacion2
    }


    @AfterEach
    public void tearDown() {
        testService.clearAll();
    }
}
