package ar.edu.unq.epersgeist.controller.dto.ubicacion;

import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = false)
public record ActualizarUbicacionDTO(@NotBlank String nombre, @NotNull Integer energia) {

    public static ActualizarUbicacionDTO desdeModelo(Ubicacion ubicacion) {
        return new ActualizarUbicacionDTO(ubicacion.getNombre(),
                                          ubicacion.getFlujoEnergia());
    }
}
