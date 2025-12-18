package ar.edu.unq.epersgeist.controller.dto.ubicacion;

import ar.edu.unq.epersgeist.modelo.ubicacion.Coordenadas;
import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties
public record CrearUbicacionDTO(@NotBlank String nombre,
                                @NotNull Integer energia,
                                @NotNull TipoUbicacionDTO tipo,
                                @NotNull
                                @Size(min = 3, max = 3, message = "El área debe tener exactamente 3 vértices")
                                @Valid
                                Set<CoordenadasDTO> vertices) {

    public static CrearUbicacionDTO desdeModelo(Ubicacion ubicacion) {

        Set<CoordenadasDTO> verticesDTO = ubicacion.getVertices().stream()
                .map(CoordenadasDTO::desdeModelo)
                .collect(Collectors.toSet());

        return new CrearUbicacionDTO(
                ubicacion.getNombre(),
                ubicacion.getFlujoEnergia(),
                TipoUbicacionDTO.desdeModelo(ubicacion.getTipo()),
                verticesDTO
        );
    }

    public Ubicacion aModelo() {

        Set<Coordenadas> verticesDominio = this.vertices.stream()
                .map(CoordenadasDTO::aModelo)
                .collect(Collectors.toSet());

        Ubicacion ubicacion = new Ubicacion(
                                    this.nombre,
                                    this.energia,
                                    this.tipo.aModelo(),
                                    verticesDominio
        );
        return ubicacion;
    }
}