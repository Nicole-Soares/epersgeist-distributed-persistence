package ar.edu.unq.epersgeist.controller.dto.espiritu;

import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = false)
public record CrearEspirituDTO(
        @NotBlank String nombre,
        @NotNull TipoEspirituDTO tipo,
        @NotNull Long ubicacionId,
        @NotNull Double hostilidad) {

    public Espiritu aModelo(Ubicacion ubicacion) {
        return tipo.crear(nombre, ubicacion, hostilidad);
    }
}