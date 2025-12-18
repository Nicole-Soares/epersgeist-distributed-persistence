package ar.edu.unq.epersgeist.controller.dto.medium;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = false)
public record CrearMediumDTO(
        @NotBlank String nombre,
        @NotNull Integer manaMax,
        @NotNull Integer mana,
        @NotNull Long ubicacionId) {

    public Medium aModelo(Ubicacion ubicacion) {
        return new Medium(this.nombre, this.manaMax, this.mana, ubicacion);
    }

}