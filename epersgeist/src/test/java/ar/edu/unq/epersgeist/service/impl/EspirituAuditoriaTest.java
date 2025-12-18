package ar.edu.unq.epersgeist.service.impl;

import ar.edu.unq.epersgeist.modelo.espiritu.Demonio;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.TipoUbicacion;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.persistence.sql.entity.espiritu.EspirituSQL;
import ar.edu.unq.epersgeist.persistence.sql.interfaces.EspirituSQLDAO;
import ar.edu.unq.epersgeist.service.interfaces.EspirituService;
import ar.edu.unq.epersgeist.service.interfaces.TestService;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EspirituAuditoriaTest extends ContainerIntegrationProviderTest {
    @Autowired
    private EspirituService espirituService;
    @Autowired
    private UbicacionService ubicacionService;
    @Autowired
    private TestService testService;
    @Autowired
    private EspirituSQLDAO espirituSQLDAO;

    private Ubicacion nuevaUbicacion;
    private Demonio nuevoEspiritu;

    @BeforeEach
    void setUp() {
        Coordenadas c1 = new Coordenadas(10.0, 10.0);
        Coordenadas c2 = new Coordenadas(30.0, 30.0);
        Coordenadas c3 = new Coordenadas(20.0, 20.0);

        Set<Coordenadas> verticesValidos = Set.of(c1, c2, c3);
        nuevaUbicacion = new Ubicacion("Ezpeleta", 50, TipoUbicacion.CEMENTERIO, verticesValidos);
        ubicacionService.create(nuevaUbicacion);

        nuevoEspiritu = new Demonio("Abel", nuevaUbicacion, 20.0);
        espirituService.create(nuevoEspiritu);
    }

    @Test
    @DisplayName("Cuando se persiste un espiritu debe establecer la fechas de creacion y update de auditoria")
    void fechasDeAuditoriaCreadasAlPersistirUnEspiritu() {
        // --- SetUp y Exercise ---
        var antesDeGuardar = java.time.Instant.now().minusSeconds(1);
        EspirituSQL espirituRecuperado = espirituSQLDAO.findById(nuevoEspiritu.getId()).orElseThrow(() -> new EntityNotFoundException("No se encontro el espiritu con id: " + nuevoEspiritu.getId())); ;

        // --- Verify ---
        assertThat(espirituRecuperado.getCreated_at())
                .as("La fecha de creacion debe ser posterior al inicio de la prueba")
                .isAfter(antesDeGuardar);

        assertThat(espirituRecuperado.getUpdated_at())
                .as("La fecha de actualizacion debe ser posterior al inicio de la prueba")
                .isAfter(antesDeGuardar);

        assertThat(espirituRecuperado.getCreated_at()).isEqualTo(espirituRecuperado.getUpdated_at());
    }

    @Test
    @DisplayName("Cuando se actualiza un espiritu debe actualizar solo la fecha de actualizacion")
    void cuandoSeActualizaDemonioDebeActualizarSoloFechaActualizacion() throws InterruptedException {
        // --- SetUp --
        EspirituSQL espirituRecuperado = espirituSQLDAO.findById(nuevoEspiritu.getId()).orElseThrow(() -> new EntityNotFoundException("No se encontro el espiritu con id: " + nuevoEspiritu.getId()));
        Date fechaCreacionOriginal = espirituRecuperado.getCreated_at();
        Date fechaActualizacionOriginal = espirituRecuperado.getUpdated_at();

        // --- Exercise ---
        Thread.sleep(100);
        Date antesDeActualizar = new Date(System.currentTimeMillis());
        nuevoEspiritu.setNombre("Demonio Modificado");
        espirituService.update(nuevoEspiritu);
        EspirituSQL espirituActualizado = espirituSQLDAO.findById(nuevoEspiritu.getId()).orElseThrow(() -> new EntityNotFoundException("No se encontro el espiritu con id: " + nuevoEspiritu.getId())); ;

        // --- Verify ---
        assertThat(espirituActualizado.getCreated_at()).isEqualTo(fechaCreacionOriginal);
        assertThat(espirituActualizado.getUpdated_at()).isAfter(fechaActualizacionOriginal);
        assertThat(espirituActualizado.getUpdated_at()).isAfter(antesDeActualizar);
    }

    @Test
    @DisplayName("Al borrarse un espiritu no es posible recuperarlo")
    void cuandoSeBorraUnEspirituNoMeLoRecuperaSiLoBusco() {
        // --- SetUp ---
        Demonio nuevoDemonio = new Demonio("DemonioAEliminar", nuevaUbicacion, 20.0);
        Espiritu espirituAEliminar = espirituService.create(nuevoDemonio);
        Long idEliminado = espirituAEliminar.getId();

        // --- Exercise ---
        espirituService.delete(idEliminado);

        // --- Verify ---
        assertThrows(EntityNotFoundException.class, () ->
                espirituService.findById(idEliminado));
    }

    @AfterEach
    public void tearDown() {
        testService.clearAll();
    }
}
