package ar.edu.unq.servicio_mensajeria.service.respuesta;

import ar.edu.unq.commons.neo.entity.ComunicacionActiva;
import ar.edu.unq.servicio_mensajeria.sql.entity.EspirituCandidato;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ServicioDeRespuestasImpl implements ServicioDeRespuestas {

    private final List<String> plantillasConPista = List.of(
            "Siento tu llamado. Mi nombre es: {PISTA}.",
            "No temas... mi nombre podría ser: {PISTA}.",
            "Escucho tus palabras. Quizás te ayude esto: {PISTA}.",
            "Has perturbado mi descanso. Mi nombre... {PISTA}."
    );

    private final List<String> plantillasSinPista = List.of(
            "¡Vete de aquí!",
            "Tu presencia me molesta.",
            "No tengo nada que decirte.",
            "El tiempo de los vivos ya pasó."
    );

    @Override
    public String construirTextoConEspiritu(EspirituCandidato espiritu, ComunicacionActiva com) {
        return construirTextoConPlantilla(espiritu, com);
    }

    @Override
    public void agregarPlantilla(String texto) {
        plantillasConPista.add(texto);
    }

    private String construirTextoConPlantilla(EspirituCandidato espiritu,
                                              ComunicacionActiva com) {

        double probabilidadDeNoDarPista =
                espiritu.getHostilidadNormalizada() * (2.0 / 3.0);

        double randomValue = Math.random();
        Random r = new Random();

        if (randomValue <= probabilidadDeNoDarPista) {
            return plantillasSinPista.get(
                    r.nextInt(plantillasSinPista.size())
            );
        }

        String plantilla = plantillasConPista.get(
                r.nextInt(plantillasConPista.size())
        );

        return aplicarPistaProgresiva(plantilla, espiritu, com);
    }

    private String aplicarPistaProgresiva(String plantilla,
                                          EspirituCandidato espiritu,
                                          ComunicacionActiva com) {

        String nombre = espiritu.getNombre();
        int nivel = com.getNivelPista();

        String pista = switch (nivel) {
            case 0 -> nombre.substring(0, Math.min(1, nombre.length()));
            case 1 -> nombre.substring(0, Math.min(3, nombre.length()));
            case 2 -> nombre.substring(0, Math.min(5, nombre.length()));
            default -> nombre;
        };

        // avanzar nivel para la próxima llamada (máximo 3)
        com.setNivelPista(Math.min(nivel + 1, 3));

        return plantilla.replace("{PISTA}", pista);
    }
}
