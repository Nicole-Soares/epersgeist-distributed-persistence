package ar.edu.unq.epersgeist.controller.dto.espiritu;

import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = false)
public record ActualizarEspirituDTO(
        @NotBlank String nombre,
        Integer nivelDeConexion
) {
    public void sobrescribir(Espiritu espiritu) {
        if (nombre != null && !nombre.isBlank()) {
            espiritu.setNombre(nombre);
        }
        if (nivelDeConexion != null) {
            int nuevoNivel = Math.max(0, Math.min(100, nivelDeConexion));
            espiritu.setNivelDeConexion(nuevoNivel);
            if (nuevoNivel <= 0) {
                espiritu.desconectarDelMediumActual();
            }
        }
    }
}

