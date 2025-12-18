package ar.edu.unq.epersgeist.controller.dto.espiritu;

import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = false)
public record ActualizarEspirituDTO(
        @NotBlank String nombre
) {
    public void sobrescribir(Espiritu espiritu) {
        espiritu.setNombre(nombre);
    }
}
