package ar.edu.unq.epersgeist.config;

import ar.edu.unq.epersgeist.controller.dto.ubicacion.CoordenadasDTO;
import ar.edu.unq.epersgeist.controller.dto.ubicacion.CrearUbicacionDTO;
import ar.edu.unq.epersgeist.controller.dto.ubicacion.TipoUbicacionDTO;
import ar.edu.unq.epersgeist.controller.dto.medium.CrearMediumDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritu.CrearEspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.espiritu.TipoEspirituDTO;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import ar.edu.unq.epersgeist.service.interfaces.UbicacionService;
import ar.edu.unq.epersgeist.service.interfaces.MediumService;
import ar.edu.unq.epersgeist.service.interfaces.EspirituService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Set;

/**
 * Inicializa datos de demo al arrancar la aplicación si la base de datos está vacía.
 * Solo se ejecuta cuando NO se está corriendo el perfil "test".
 */
@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UbicacionService ubicacionService,
            MediumService mediumService,
            EspirituService espirituService
    ) {
        return args -> {
            if (!ubicacionService.recuperarTodos().isEmpty()) {
                System.out.println("[DataInitializer] Ya existen ubicaciones, omitiendo seed.");
                return;
            }

            System.out.println("[DataInitializer] Cargando datos de demo...");

            // --- Ubicaciones ---
            Ubicacion sanctuaryHouse = ubicacionService.createDTO(new CrearUbicacionDTO(
                    "Tanglewood Street House",
                    50,
                    TipoUbicacionDTO.SANTUARIO,
                    Set.of(
                            new CoordenadasDTO(-58.2755, -34.7032),  // (longitud, latitud)
                            new CoordenadasDTO(-58.2745, -34.7052),
                            new CoordenadasDTO(-58.2765, -34.7052)
                    )
            ));

            Ubicacion graveyard = ubicacionService.createDTO(new CrearUbicacionDTO(
                    "Graveyard of Souls",
                    80,
                    TipoUbicacionDTO.CEMENTERIO,
                    Set.of(
                            new CoordenadasDTO(-58.2800, -34.7090),  // (longitud, latitud)
                            new CoordenadasDTO(-58.2790, -34.7110),
                            new CoordenadasDTO(-58.2810, -34.7110)
                    )
            ));

            Ubicacion asylum = ubicacionService.createDTO(new CrearUbicacionDTO(
                    "Asylum Sanctuary",
                    30,
                    TipoUbicacionDTO.SANTUARIO,
                    Set.of(
                            new CoordenadasDTO(-58.2850, -34.7140),  // (longitud, latitud)
                            new CoordenadasDTO(-58.2840, -34.7160),
                            new CoordenadasDTO(-58.2860, -34.7160)
                    )
            ));

            // Conectar en grafo Neo4j bidireccionalmente para acceso libre entre todas las zonas
            ubicacionService.conectar(sanctuaryHouse.getId(), graveyard.getId(), 10L);
            ubicacionService.conectar(graveyard.getId(), sanctuaryHouse.getId(), 10L);

            ubicacionService.conectar(graveyard.getId(), asylum.getId(), 15L);
            ubicacionService.conectar(asylum.getId(), graveyard.getId(), 15L);

            ubicacionService.conectar(sanctuaryHouse.getId(), asylum.getId(), 25L);
            ubicacionService.conectar(asylum.getId(), sanctuaryHouse.getId(), 25L);

            // --- Mediums ---
            var m1 = mediumService.create(new CrearMediumDTO(
                    "John Constantine", 100, 100, sanctuaryHouse.getId()
            ));

            var m2 = mediumService.create(new CrearMediumDTO(
                    "Lorraine Warren", 90, 80, graveyard.getId()
            ));

            // --- Espíritus ---
            espirituService.create(new CrearEspirituDTO(
                    "Banshee Demoniaca", TipoEspirituDTO.DEMONIO, graveyard.getId(), 85.0
            ));

            espirituService.create(new CrearEspirituDTO(
                    "Ángel Sanador", TipoEspirituDTO.ANGELICAL, sanctuaryHouse.getId(), 10.0
            ));

            espirituService.create(new CrearEspirituDTO(
                    "Poltergeist Agresivo", TipoEspirituDTO.DEMONIO, graveyard.getId(), 95.0
            ));

            System.out.println("[DataInitializer] ✓ Datos de demo cargados exitosamente.");
        };
    }
}
