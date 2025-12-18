package ar.edu.unq.epersgeist.service.impl;


import ar.edu.unq.epersgeist.exception.DiferenteUbicacionException;
import ar.edu.unq.epersgeist.exception.DominacionInvalidaException;
import ar.edu.unq.epersgeist.exception.EspirituConectadoException;
import ar.edu.unq.epersgeist.exception.MaximoNivelConexionException;
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
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EspirituServiceImplTest extends ContainerIntegrationProviderTest {

    @Autowired
    private EspirituService espirituService;

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private MediumService mediumService;

    @Autowired
    private TestService testService;

    private Espiritu angel;
    private Espiritu demonio;
    private Medium medium;
    private Ubicacion cementerioPersistido;
    private Ubicacion santuarioPersistido;
    private Set<Coordenadas> coordenadasSet;
    private Set<Coordenadas> coordenadasSet2;

    @BeforeEach
    void setUp() {
        // Set 1: Coordenadas distintas
        Coordenadas coordenadas = new Coordenadas(10, 10);
        Coordenadas coordenadas2 = new Coordenadas(20, 20);
        Coordenadas coordenadas3 = new Coordenadas(30, 30);

        // Set 2: Coordenadas que NO deben ser duplicadas (porque el @Data hace el equals y hash en base a los atributos)
        Coordenadas coordenadas4 = new Coordenadas(40, 40);
        Coordenadas coordenadas5 = new Coordenadas(50, 50);
        Coordenadas coordenadas6 = new Coordenadas(60, 60);

        // Vertices
        this.coordenadasSet = Set.of(coordenadas, coordenadas2, coordenadas3);
        this.coordenadasSet2 = Set.of(coordenadas4, coordenadas5, coordenadas6);

        cementerioPersistido = new Ubicacion("cementerio de chacarita", 50, TipoUbicacion.CEMENTERIO, coordenadasSet);
        santuarioPersistido = new Ubicacion("santuario", 25, TipoUbicacion.SANTUARIO, coordenadasSet2);
        ubicacionService.create(cementerioPersistido);
        ubicacionService.create(santuarioPersistido);

        angel = new Angel("Gabriel", santuarioPersistido, 20.0);
        demonio = new Demonio("Pablo", cementerioPersistido, 20.0);
        espirituService.create(demonio);
        espirituService.create(angel);

        medium = new Medium("Damian", 750, 69, cementerioPersistido);
        mediumService.create(medium);
    }

    // ----------- MÉTODOS AUXILIARES PARA TESTS DE PAGINACIÓN -------------

    private void crearEspiritusParaPaginacion() {
        // Los demonios para la paginacion
        List<Demonio> demonios = Arrays.asList(
                new Demonio("Belial", cementerioPersistido, 20.0),
                new Demonio("Malak", cementerioPersistido, 20.0),
                new Demonio("Kazimir", cementerioPersistido, 20.0),
                new Demonio("Azrael", cementerioPersistido, 20.0),
                new Demonio("Vex", cementerioPersistido, 20.0),
                new Demonio("Lilith", cementerioPersistido, 20.0),
                new Demonio("Asmodeo", cementerioPersistido, 20.0)
        );

        // Nivel de conexion para el orden
        int[] niveles = {95, 80, 75, 70, 60, 50, 30};

        // Seteamos el nivel de conexion y persistimos en una sola operacion por cada demonio
        for (int i = 0; i < demonios.size(); i++) {
            Demonio demonio = demonios.get(i);
            demonio.setNivelDeConexion(niveles[i]);
            espirituService.create(demonio);
        }

        // Otros espiritus que no son demonios
        espirituService.create(new Angel("Orion", santuarioPersistido, 20.0));
        espirituService.create(new Angel("Elias", santuarioPersistido, 20.0));
        espirituService.create(new Angel("Ariel", santuarioPersistido, 20.0));
    }


    // ---------------- CREATE --------------

    @Test
    @DisplayName("Create: Se puede crear un espíritu y se le asigna un ID")
    void crear() {
        // --- Verify ---
        assertNotNull(demonio);
        assertNotNull(demonio.getId());
        assertEquals("Pablo", demonio.getNombre());
        assertEquals("cementerio de chacarita", demonio.getUbicacion().getNombre());
    }

    @Test
    @DisplayName("Create: No se puede crear espiritu sin ubicacion")
    void crearFallidoSinUbicacion() {
        // --- Verify ---
        assertThrows(NullPointerException.class, () -> {
            new Demonio("Mazikeen", null, 20.0);
        });
    }

    @Test
    @DisplayName("Create: No se puede crear espiritu sin nombre")
    void crearFallidoSinNombre() {
        // --- Verify ---
        assertThrows(NullPointerException.class, () -> {
            new Demonio(null , cementerioPersistido, 20.0);
        });
    }

    @Test
    @DisplayName("Create: No se puede crear espiritu si su ubicacion no esta persistida")
    void crearFallidoUbicacionSinPersistencia() {
        // --- SetUp ---
        Ubicacion ubiNoPersistida = new Ubicacion("Recoleta", 20, TipoUbicacion.CEMENTERIO, coordenadasSet);

        // --- Verify ---
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            espirituService.create(new Demonio("Lilith", ubiNoPersistida, 20.0));
        });
    }



    // ---------------- READ -----------------


    @Test
    @DisplayName("Read: Se puede recuperar un espíritu por su ID")
    void findById() {
        // --- Exercise ---
        Espiritu angelRecuperado = espirituService.findById(angel.getId());

        // --- Verify ---
        assertNotNull(angelRecuperado);
        assertEquals(angel.getId(), angelRecuperado.getId());
    }

    @Test
    @DisplayName("Read: No se puede recuperar porque espiritu con ese id no existe en la base de datos")
    void findByIdYDevuelveNull() {
        // --- Verify ---
        assertThrows(EntityNotFoundException.class, () -> {
            espirituService.findById(9999L);
        });
    }

    @Test
    @DisplayName("Read: Se pueden recuperar todos los espíritus existentes")
    void recuperarTodos() {
        // --- Exercise ---
        var todos = espirituService.recuperarTodos();

        // --- Verify ---
        assertTrue(todos.size() >= 2);
        assertTrue(todos.stream().anyMatch(e -> e.getId().equals(angel.getId())));
        assertTrue(todos.stream().anyMatch(e -> e.getId().equals(demonio.getId())));
    }

    @Test
    @DisplayName("Read: Recuperar todos es vacio")
    void recuperarTodosVacio() {
        // --- SetUp ---
        espirituService.delete(angel.getId());
        espirituService.delete(demonio.getId());

        // --- Exercise ---
        var listaVacia = espirituService.recuperarTodos();

        // --- Verify ---
        assertEquals(0, listaVacia.size());
    }


    // --------------- UPDATE -----------------

    @Test
    @DisplayName("Update: Se puede actualizar la información de un espíritu existente")
    void update() {
        // --- SetUp ---
        demonio.setNivelDeConexion(50);

        // --- Excercise ---
        Espiritu demonioActualizado = espirituService.update(demonio);

        // --- Verify ---
        assertNotNull(demonioActualizado);
        assertNotNull(demonioActualizado.getId());
        assertEquals("Pablo", demonioActualizado.getNombre());
        assertEquals(50, demonioActualizado.getNivelDeConexion());
    }

    @Test
    @DisplayName("Update: al persitir entidad transient, update lo guarda y toma ambas instancias por igual")
    void updateEspirituNoPersistidoQuedaSincronizadoConPersist() {
        // --- SetUp ---
        Espiritu demonioTransient = new Demonio("Mazikeen", cementerioPersistido, 20.0);

        // --- Validacion del estado inicial ---
        assertNull(demonioTransient.getId());

        // --- Exercise ---
        Espiritu demonioPersist = espirituService.update(demonioTransient);

        // --- Verify ---
        assertNotNull(demonioTransient.getId());
        assertNotNull(demonioPersist.getId());
        assertEquals(0, demonioTransient.getNivelDeConexion());
        assertEquals(0, demonioPersist.getNivelDeConexion());

        // --- Exercise adicional ---
        demonioTransient.setNivelDeConexion(50);

        // --- Verify adicional ---
        assertEquals(50, demonioTransient.getNivelDeConexion());
        assertEquals(50, demonioPersist.getNivelDeConexion());
    }


    // ---------------- DELETE -----------------

    @Test
    @DisplayName("Delete: Se puede eliminar un espíritu existente")
    void eliminar() {
        // --- Exercise ---
        espirituService.delete(angel.getId());

        // --- Verify ---
        assertThrows(EntityNotFoundException.class, () -> {
            espirituService.findById(angel.getId());
        });
    }

    @Test
    @DisplayName("Delete: Se puede eliminar un espíritu conectado a un medium")
    void eliminarEspirituConMedium() {
        // --- Verificacion inicial ---
        assertTrue(demonio.esLibre());

        //--- SetUp ---
        espirituService.conectar(demonio.getId(), medium.getId());
        Espiritu demonioConectado = espirituService.findById(demonio.getId());
        List<Espiritu> espiritusMediumConectado = mediumService.espiritus(medium.getId());

        // --- Segunda verificacion adicional ---
        assertFalse(demonioConectado.esLibre());
        assertEquals(espiritusMediumConectado.getFirst().getId(), demonio.getId());

        // --- Exercise ---
        espirituService.delete(demonio.getId());
        mediumService.update(medium);
        List<Espiritu> espiritusMediumDesconectado = mediumService.espiritus(medium.getId());

        // --- Verify ---
        assertEquals(0, espiritusMediumDesconectado.size());
        assertThrows(EntityNotFoundException.class, () -> {
            espirituService.findById(demonio.getId());
        });
    }


    // ---------------- MÉTODOS ADICIONALES -----------------


    @Test
    @DisplayName("Conectar: espíritu y médium en la misma ubicación y libre → aumenta nivel de conexión del espiritu")
    void conectarEspirituYMediumMismaUbicacionYLibreFortaleceNivel() {
        // --- Verificacion inicial ---
        assertTrue(demonio.esLibre());

        // --- SetUp y Exercise ---
        int nivelInicial = demonio.getNivelDeConexion();
        Medium mediumConectado = espirituService.conectar(demonio.getId(), medium.getId());
        Espiritu demonioActualizado = espirituService.findById(demonio.getId());
        List<Espiritu> espirituDelMedium = espirituService.espiritusDelMedium(mediumConectado.getId());

        // --- Verificacion adicional ---
        assertTrue(espirituDelMedium.stream().anyMatch(espiritu -> espiritu.getId().equals(demonioActualizado.getId())));

        // --- Exercise final ---
        int esperado = nivelInicial + (int) (medium.getMana() * 0.20);

        // --- Verify ---
        assertEquals(esperado, demonioActualizado.getNivelDeConexion());
        assertFalse(demonioActualizado.esLibre());
    }

    @Test
    @DisplayName("Conectar: falla si el espíritu ya está conectado")
    void conectarEspirituYaConectadoLanzaExcepcion() {
        // --- Exercise ---
        espirituService.conectar(demonio.getId(), medium.getId());

        // --- Verify ---
        assertThrows(EspirituConectadoException.class, () ->
                espirituService.conectar(demonio.getId(), medium.getId())
        );
    }

    @Test
    @DisplayName("Conectar: falla si espíritu y médium están en distintas ubicaciones")
    void conectarEspirituEnOtraUbicacionLanzaExcepcion() {
        // --- Verify ---
        assertThrows(DiferenteUbicacionException.class, () ->
                espirituService.conectar(angel.getId(), medium.getId())
        );
    }

    @Test
    @DisplayName("Conectar: falla si se supera el nivel máximo de conexión")
    void conectarEspirituSuperaNivelMaximoLanzaExcepcion() {
        // --- SetUp y Exercise ---
        medium.setMana(550);
        Medium mediumActualizado = mediumService.update(medium);

        // --- Verify ---
        assertThrows(MaximoNivelConexionException.class, () ->
                espirituService.conectar(demonio.getId(), mediumActualizado.getId())
        );
    }

    @Test
    @DisplayName("Conectar: falla si el ID de espíritu no existe")
    void conectarEspirituInexistenteLanzaExcepcion() {
        // --- Verify ---
        assertThrows(EntityNotFoundException.class, () ->
                espirituService.conectar(9999L, medium.getId())
        );
    }

    @Test
    @DisplayName("Conectar: falla si el ID de médium no existe")
    void conectarMediumInexistenteLanzaExcepcion() {
        // --- Verify ---
        assertThrows(EntityNotFoundException.class, () ->
                espirituService.conectar(demonio.getId(), 9999L)
        );
    }

    @Test
    @DisplayName("EsLibre: es true si y solo si medium es null")
    void espirituEsLibreConsistenteConMedium() {
        // --- Verificacion inicial ---
        assertTrue(demonio.esLibre());

        // --- SetUp y Exercise ---
        espirituService.conectar(demonio.getId(), medium.getId());
        Espiritu demonioActualizado = espirituService.findById(demonio.getId());

        // --- Verify ---
        assertFalse(demonioActualizado.esLibre());
    }

    @Test
    @DisplayName("Paginación: página 1 en orden ascendente devuelve 3 demonios ordenados de menor a mayor conexión")
    void espiritusDemoniacosAscendentePaginaUnoDevuelveTresDemonios() {
        // --- SetUp ---
        this.crearEspiritusParaPaginacion();
        Pageable primeraPaginaASC = PageRequest.of(0, 3, Sort.by("nivelDeConexion").ascending());

        // --- Exercise ---
        Page<Demonio> pagina = espirituService.espiritusDemoniacos(primeraPaginaASC);

        // --- Verify ---
        assertNotNull(pagina);
        assertEquals(3, pagina.getContent().size(), "La primera pagina debe tener 3 demonios");
        assertEquals(8, pagina.getTotalElements(), "Debe haber un total de 8 demonios");
        assertEquals(0, pagina.getNumber(), "La pagina debe ser la numero 0");
        assertEquals("Pablo", pagina.getContent().getFirst().getNombre());
        assertTrue(pagina.getContent().getFirst().getNivelDeConexion() < pagina.getContent().get(1).getNivelDeConexion());
        assertTrue(pagina.getContent().get(1).getNivelDeConexion() < pagina.getContent().get(2).getNivelDeConexion());
    }

    @Test
    @DisplayName("Paginación: página 2 en orden ascendente devuelve 3 demonios ordenados de menor a mayor conexión")
    void espiritusDemoniacosAscendentePaginaDosDevuelveTresDemonios() {
        // --- SetUp ---
        this.crearEspiritusParaPaginacion();
        Pageable segundaPaginaASC = PageRequest.of(1, 3, Sort.by("nivelDeConexion").ascending());

        // --- Exercise ---
        Page<Demonio> pagina = espirituService.espiritusDemoniacos(segundaPaginaASC);

        // --- Verify ---
        assertNotNull(pagina);
        assertEquals(3, pagina.getContent().size(), "La primera pagina debe tener 3 demonios");
        assertEquals(8, pagina.getTotalElements(), "Debe haber un total de 8 demonios");
        assertEquals(1, pagina.getNumber(), "La pagina debe ser la numero 1");
        assertEquals("Vex", pagina.getContent().getFirst().getNombre());
        assertTrue(pagina.getContent().getFirst().getNivelDeConexion() < pagina.getContent().get(1).getNivelDeConexion());
        assertTrue(pagina.getContent().get(1).getNivelDeConexion() < pagina.getContent().get(2).getNivelDeConexion());
    }

    @Test
    @DisplayName("Paginación: página 3 en orden ascendente devuelve 3 demonios ordenados de menor a mayor conexión")
    void espiritusDemoniacosAscendentePaginaTresDevuelveDosDemonios() {
        // --- SetUp ---
        this.crearEspiritusParaPaginacion();
        Pageable terceraPaginaASC = PageRequest.of(2, 3, Sort.by("nivelDeConexion").ascending());

        // --- Exercise ---
        Page<Demonio> pagina = espirituService.espiritusDemoniacos(terceraPaginaASC);

        // --- Verify ---
        assertNotNull(pagina);
        assertEquals(2, pagina.getContent().size(), "La primera pagina debe tener 3 demonios");
        assertEquals(8, pagina.getTotalElements(), "Debe haber un total de 8 demonios");
        assertEquals(2, pagina.getNumber(), "La pagina debe ser la numero 1");
        assertEquals("Malak", pagina.getContent().getFirst().getNombre());
        assertTrue(pagina.getContent().getFirst().getNivelDeConexion() < pagina.getContent().get(1).getNivelDeConexion());
    }

    @Test
    @DisplayName("Paginación: página 1 en orden descendente devuelve 3 demonios ordenados de mayor a menor conexión")
    void espiritusDemoniacosDescendentePaginaUnoDevuelveTresDemonios() {
        // --- SetUp ---
        this.crearEspiritusParaPaginacion();
        Pageable primeraPaginaDESC = PageRequest.of(0, 3, Sort.by("nivelDeConexion").descending());

        // --- Exercise ---
        Page<Demonio> pagina = espirituService.espiritusDemoniacos(primeraPaginaDESC);

        // --- Verify ---
        assertNotNull(pagina);
        assertEquals(3, pagina.getContent().size(), "La primera pagina debe tener 3 demonios");
        assertEquals(8, pagina.getTotalElements(), "Debe haber un total de 8 demonios");
        assertEquals(0, pagina.getNumber(), "La pagina debe ser la numero 1");
        assertEquals("Belial", pagina.getContent().getFirst().getNombre());
        assertTrue(pagina.getContent().getFirst().getNivelDeConexion() > pagina.getContent().get(1).getNivelDeConexion());
        assertTrue(pagina.getContent().get(1).getNivelDeConexion() > pagina.getContent().get(2).getNivelDeConexion());
    }

    @Test
    @DisplayName("Paginación: página fuera del rango de la cantidad de páginas devuelve lista vacía")
    void espiritusDemoniacosDescendentePaginaFueraDeRangoDevuelveVacio() {
        // --- SetUp ---
        this.crearEspiritusParaPaginacion();
        Pageable paginaFueraRango = PageRequest.of(20, 3, Sort.by("nivelDeConexion").descending());

        // --- Exercise ---
        Page<Demonio> pagina = espirituService.espiritusDemoniacos(paginaFueraRango);

        // --- Verify ---
        assertTrue(pagina.isEmpty());
        assertThrows(NoSuchElementException.class,
                () -> pagina.getContent().getFirst());
    }

    @Test
    @DisplayName("Paginación: página < 0 lanza excepción")
    void espiritusDemoniacosPaginaMenorACeroLanzaExcepcion() {
        // --- SetUp y Exercise ---
        this.crearEspiritusParaPaginacion();

        // --- Verify ---
        assertThrows(IllegalArgumentException.class, () -> PageRequest.of(-1, 3, Sort.by("nivelDeConexion").descending()));
        assertThrows(IllegalArgumentException.class, () -> PageRequest.of(-1, 3, Sort.by("nivelDeConexion").ascending()));
    }

    @Test
    @DisplayName("Paginación: cantidad por página < 0 lanza excepción")
    void espiritusDemoniacosCantidadMenorACeroLanzaExcepcion() {
        // --- SetUp y Exercise ---
        this.crearEspiritusParaPaginacion();

        // --- Verify ---
        assertThrows(IllegalArgumentException.class, () -> PageRequest.of(0, -1, Sort.by("nivelDeConexion").descending()));
        assertThrows(IllegalArgumentException.class, () -> PageRequest.of(0, -1, Sort.by("nivelDeConexion").ascending()));
    }

    @Test
    @DisplayName("Paginación: no trae angeles a la tabla resultante")
    void espiritusDemoniacosNoDevuelveAngeles() {
        // --- SetUp y Exercise ---
        this.crearEspiritusParaPaginacion();
        Pageable primeraPaginaDESC = PageRequest.of(0, 3, Sort.by("nivelDeConexion").descending());
        Page<Demonio> pagina = espirituService.espiritusDemoniacos(primeraPaginaDESC);

        // --- Verify ---
        assertTrue(pagina.stream().allMatch(e -> e instanceof Demonio));
    }

    @Test
    @DisplayName("Paginación: cantidad por página mayor que total de demonios devuelve todos")
    void espiritusDemoniacosCantidadMayorQueTotalDevuelveTodos() {
        // --- SetUp y Exercise ---
        this.crearEspiritusParaPaginacion();
        Pageable paginaConTodos = PageRequest.of(0, 100, Sort.by("nivelDeConexion").descending());
        Page<Demonio> pagina = espirituService.espiritusDemoniacos(paginaConTodos);
        long cantidadDemonios = espirituService.recuperarTodos()
                .stream()
                .filter(e -> e instanceof Demonio)
                .count();

        // --- Verify ---
        assertEquals(cantidadDemonios, pagina.getContent().size());
    }

    // ---------------- DOMINIO DE ESPIRITUS -----------------

    @Test
    @DisplayName("Dominio de Espíritus: Un espíritu puede dominar a otro dentro de la misma ubicación y rango")
    void espirituPuedeDominarAOtroEnMismaUbicacionDentroDelRango() {
        // --- SetUp ---
        Set<Coordenadas> coords = Set.of(
                new Coordenadas(-58.3816, -34.6037),
                new Coordenadas(-58.3800, -34.6030),
                new Coordenadas(-58.3830, -34.6040)
        );

        Ubicacion ubicacionA = new Ubicacion("Ebisugaoka", 20, TipoUbicacion.SANTUARIO, coords);
        ubicacionService.create(ubicacionA);

        Espiritu espirituADominar = new Angel ("Shimizu Hinako", ubicacionA, 20.0);
        Espiritu espirituDominante = new Demonio ("Pyramid Head", ubicacionA, 20.0);

        // Coordenadas separadas = 2.7 km
        espirituDominante.setCoordenadas(new Coordenadas(-58.3816, -34.6037));
        espirituADominar.setCoordenadas(new Coordenadas(-58.3816, -34.6280));

        espirituService.create(espirituDominante);
        espirituService.create(espirituADominar);

        // --- Exercise ---
        espirituService.dominar(espirituDominante.getId(), espirituADominar.getId());
        Espiritu espirituADominarActualizado = espirituService.findById(espirituADominar.getId());

        // --- Verify ---
        assertNotNull(espirituADominarActualizado.getEspirituDominante());
        assertEquals(espirituDominante.getId(), espirituADominarActualizado.getEspirituDominante().getId());
    }

    @Test
    @DisplayName("Dominio de Espíritus: Un espíritu puede tener un espíritu dominante asignado desde dos ubicaciones diferentes")
    void espirituPuedeDominarAOtroEnDistintasUbicacionesDentroDelRango() {
        // --- SetUp ---
        Set<Coordenadas> coordsA = Set.of(
                new Coordenadas(-58.3816, -34.6037),
                new Coordenadas(-58.3800, -34.6030),
                new Coordenadas(-58.3830, -34.6040)
        );
        Set<Coordenadas> coordsB = Set.of(
                new Coordenadas(-58.3816, -34.6287),
                new Coordenadas(-58.3800, -34.6290),
                new Coordenadas(-58.3830, -34.6270)
        );

        Ubicacion ubicacionA = new Ubicacion("Midwich Elementary School", 20, TipoUbicacion.SANTUARIO, coordsA);
        Ubicacion ubicacionB = new Ubicacion("Nowhere", 20, TipoUbicacion.CEMENTERIO, coordsB);

        ubicacionService.create(ubicacionA);
        ubicacionService.create(ubicacionB);

        Espiritu espirituDominante = new Angel("Cheryl", ubicacionA, 20.0);
        Espiritu espirituADominar = new Demonio("James", ubicacionB, 20.0);

        Espiritu espirituDominanteCreado = espirituService.create(espirituDominante);
        Espiritu espirituADominarCreado = espirituService.create(espirituADominar);

        // Coordenadas separadas = 2.7 km
        espirituDominante.setCoordenadas(new Coordenadas(-58.3816, -34.6037));
        espirituADominar.setCoordenadas(new Coordenadas(-58.3816, -34.6280));

        Espiritu espirituDominanteActualizado = espirituService.findById(espirituDominanteCreado.getId());
        Espiritu espirituADominarActualizado = espirituService.findById(espirituADominarCreado.getId());

        // --- Exercise ---
        espirituService.dominar(espirituDominanteActualizado.getId(), espirituADominarActualizado.getId());
        Espiritu espirituDominado = espirituService.findById(espirituADominarActualizado.getId());

        // --- Verify ---
        assertNotNull(espirituDominado.getEspirituDominante());
        assertEquals(espirituDominanteCreado.getId(), espirituDominado.getEspirituDominante().getId());
    }

    @Test
    @DisplayName("Dominio de Espíritus: Un espíritu no puede dominar a otro si está fuera de rango y lanza excepción")
    void espirituNoPuedeDominarEspirituFueraDeRangoYLanzaExcepcion() {

        // --- SetUp ---
        final double BASE_LAT = -34.6037;
        final double BASE_LON = -58.3816;

        Coordenadas coords = new Coordenadas(BASE_LON, BASE_LAT);

        angel.setCoordenadas(coords);
        demonio.setCoordenadas(coords);

        espirituService.update(angel);
        espirituService.update(demonio);

        Espiritu angelPersistido = espirituService.findById(angel.getId());
        Espiritu demonioPersistido = espirituService.findById(demonio.getId());

        // --- Exercise & Verify ---

        Exception exception = assertThrows(DominacionInvalidaException.class, () -> {
            espirituService.dominar(angelPersistido.getId(), demonioPersistido.getId());
        });
    }

    @Test
    @DisplayName("Dominio de Espíritus: Un espíritu no puede dominar a otro si el dominado no es libre y lanza excepción")
    void espirituNoPuedeDominarEspirituNoLibreYLanzaExcepcion() {

        // --- SetUp ---
        medium.conectarseAEspiritu(demonio);
        mediumService.update(medium);

        final double BASE_LAT = -34.6037;
        final double BASE_LON = -58.3816;

        angel.setCoordenadas(new Coordenadas(BASE_LON, BASE_LAT));
        demonio.setCoordenadas(new Coordenadas(BASE_LON, BASE_LAT));

        espirituService.update(angel);
        espirituService.update(demonio);

        Espiritu angelDominante = espirituService.findById(angel.getId());
        Espiritu demonioObjetivo = espirituService.findById(demonio.getId());

        // --- Exercise & Verify ---

        Exception exception = assertThrows(DominacionInvalidaException.class, () -> {
            espirituService.dominar(angelDominante.getId(), demonioObjetivo.getId());
        });
    }

    @AfterEach
    public void tearDown() {
        testService.clearAll();
    }
}

