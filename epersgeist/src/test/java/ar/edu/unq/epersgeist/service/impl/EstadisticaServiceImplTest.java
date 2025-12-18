package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.epersgeist.exception.NoExistenSantuarioCorrompidoException;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.TipoUbicacion;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.modelo.estadistica.ReporteSantuarioMasCorrupto;
import ar.edu.unq.epersgeist.service.interfaces.EspirituService;
import ar.edu.unq.epersgeist.service.interfaces.MediumService;
import ar.edu.unq.epersgeist.service.interfaces.TestService;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EstadisticaServiceImplTest extends ContainerIntegrationProviderTest {

    @Autowired
    private EstadisticaServiceImpl estadisticaService;
    @Autowired
    private MediumService mediumService;
    @Autowired
    private EspirituService espirituService;
    @Autowired
    private UbicacionService ubicacionService;
    @Autowired
    private TestService testService;

    private Ubicacion santuario;
    private Medium mediumSuarez;
    private Espiritu demonioSilithur;
    private Espiritu demonioSpeltergeist;
    private Espiritu angelSagrado;
    private Set<Coordenadas> coordenadasSet;
    private Set<Coordenadas> coordenadasSet2;

    @BeforeEach
    void setUp() {
        Coordenadas coordenadas = new Coordenadas(30, 30);
        Coordenadas coordenadas2 = new Coordenadas(50, 50);
        Coordenadas coordenadas3 = new Coordenadas(60, 60);
        Coordenadas coordenadas4 = new Coordenadas(70, 70);
        Coordenadas coordenadas5 = new Coordenadas(80, 80);
        Coordenadas coordenadas6 = new Coordenadas(90, 90);
        coordenadasSet = Set.of(coordenadas, coordenadas2, coordenadas3);
        coordenadasSet2 = Set.of(coordenadas4, coordenadas5, coordenadas6);

        santuario = new Ubicacion("Santuario", 100, TipoUbicacion.SANTUARIO, coordenadasSet);
        ubicacionService.create(santuario);

        mediumSuarez = new Medium("Marco Suarez", 100, 100, santuario);
        mediumService.create(mediumSuarez);

        demonioSilithur = new Demonio("Demonio Silithur", santuario, 20.0);
        espirituService.create(demonioSilithur);

        demonioSpeltergeist = new Demonio("Demonio Speltergeist", santuario, 20.0);
        espirituService.create(demonioSpeltergeist);

        espirituService.conectar(demonioSilithur.getId(), mediumSuarez.getId());

        angelSagrado = new Angel("Angel Sagrado", santuario, 20.0);
        espirituService.create(angelSagrado);
    }


    @Test
    @DisplayName("santuarioCorrupto: El reporte del santuario más corrupto se genera correctamente")
    void santuarioCorruptoDevuelveReporteCorrecto() {
        // --- Exercise ---
        ReporteSantuarioMasCorrupto reporte = estadisticaService.santuarioCorrupto();

        // --- Verify ---
        assertNotNull(reporte);
        assertEquals("Santuario", reporte.nombreSantuario());
        assertEquals(mediumSuarez.getId(), reporte.mediumConMasDemonios().getId());
        assertEquals(2, reporte.cantidadTotalDemonios());
        assertEquals(1, reporte.cantidadDemoniosLibres());
    }


    @Test
    @DisplayName("santuarioCorrupto: Devuelve null o lanza excepción si no hay santuarios")
    void santuarioCorruptoSinSantuarios() {
        testService.clearAll();
        assertThrows(NoExistenSantuarioCorrompidoException.class, () -> estadisticaService.santuarioCorrupto());
    }


    @Test
    @DisplayName("santuarioCorrupto: Devuelve el santuario con más corrupto aunque haya varios santuarios")
    void santuarioCorruptoConMultiplesSantuarios() {
        // --- Setup ---
        Ubicacion otroSantuario = new Ubicacion("Otro Santuario", 100, TipoUbicacion.SANTUARIO, coordenadasSet2);
        ubicacionService.create(otroSantuario);

        Medium mediumSores = new Medium("Maria Sores", 100, 100, otroSantuario);
        mediumService.create(mediumSores);

        Espiritu demonioSilithar = new Demonio("Demonio Silithar", otroSantuario, 20.0);
        espirituService.create(demonioSilithar);
        espirituService.conectar(demonioSilithar.getId(), mediumSores.getId());

        Espiritu angelSanto = new Angel("Angel Santo", otroSantuario, 20.0);
        espirituService.create(angelSanto);


        // --- Exercise ---
        ReporteSantuarioMasCorrupto reporte = estadisticaService.santuarioCorrupto();


        // --- Verify ---
        assertNotNull(reporte);
        assertEquals("Santuario", reporte.nombreSantuario());
        assertEquals(mediumSuarez.getId(), reporte.mediumConMasDemonios().getId());
        assertEquals(2, reporte.cantidadTotalDemonios());
        assertEquals(1, reporte.cantidadDemoniosLibres());
    }


    @Test
    @DisplayName("santuarioCorrupto: Devuelve el medium con más demonios aunque haya varios mediums en el santuario")
    void santuarioCorruptoConMultiplesMediumsEnUnSantuario() {
        // --- Setup ---
        Medium mediumSomez = new Medium("Max Somez", 100, 100, santuario);
        mediumService.create(mediumSomez);

        Espiritu demonioXyz = new Demonio("Demonio Zyx", santuario, 20.0);
        espirituService.create(demonioXyz);
        espirituService.conectar(demonioXyz.getId(), mediumSomez.getId());

        espirituService.conectar(demonioSpeltergeist.getId(), mediumSomez.getId());


        // --- Exercise ---
        ReporteSantuarioMasCorrupto reporte = estadisticaService.santuarioCorrupto();


        // --- Verify ---
        assertNotNull(reporte);
        assertEquals("Santuario", reporte.nombreSantuario());
        assertEquals(mediumSomez.getId(), reporte.mediumConMasDemonios().getId());
        assertEquals(3, reporte.cantidadTotalDemonios());
        assertEquals(0, reporte.cantidadDemoniosLibres());
    }


    @Test
    @DisplayName("santuarioCorrupto: Ignora ubicaciones que no son santuarios")
    void santuarioCorruptoIgnoraNoSantuarios() {
        // --- Setup ---
        Coordenadas coordenadas = new Coordenadas(90,90);
        Coordenadas coordenadas2 = new Coordenadas(10,10);
        Coordenadas coordenadas3 = new Coordenadas(20,20);
        Set<Coordenadas> coordenadasSet3 = Set.of(coordenadas, coordenadas2, coordenadas3);
        Ubicacion cementerio = new Ubicacion("Cementerio", 100, TipoUbicacion.CEMENTERIO, coordenadasSet3);
        ubicacionService.create(cementerio);

        Medium mediumPerez = new Medium("Juan Perez", 100, 100, cementerio);
        mediumService.create(mediumPerez);

        Espiritu demonioXyz = new Demonio("Demonio Xyz", cementerio, 20.0);
        espirituService.create(demonioXyz);
        espirituService.conectar(demonioXyz.getId(), mediumPerez.getId());

        Espiritu demonioAbc = new Demonio("Demonio Abc", cementerio, 20.0);
        espirituService.create(demonioAbc);
        espirituService.conectar(demonioAbc.getId(), mediumPerez.getId());

        Espiritu demonioDef = new Demonio("Demonio Def", cementerio, 20.0);
        espirituService.create(demonioDef);


        // --- Exercise ---
        ReporteSantuarioMasCorrupto reporte = estadisticaService.santuarioCorrupto();


        // --- Verify ---
        assertNotNull(reporte);
        assertEquals("Santuario", reporte.nombreSantuario());
        assertEquals(mediumSuarez.getId(), reporte.mediumConMasDemonios().getId());
        assertEquals(2, reporte.cantidadTotalDemonios());
        assertEquals(1, reporte.cantidadDemoniosLibres());
    }

    @AfterEach
    void tearDown() {
        testService.clearAll();
    }
}