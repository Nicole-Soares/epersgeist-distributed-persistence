package ar.edu.unq.epersgeist.controller.dto.ubicacion;

import ar.edu.unq.epersgeist.modelo.ubicacion.Ubicacion;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record RecuperarUbicacionDTO(Long id,
                                    TipoUbicacionDTO tipo,
                                    String nombre,
                                    Integer energia,
                                    Map<Long, Long> conexiones,
                                    Set<CoordenadasDTO> vertices,
                                    Integer temperatura) {

    public static RecuperarUbicacionDTO desdeModelo(Ubicacion ubicacion) {
        return new RecuperarUbicacionDTO(
                ubicacion.getId(),
                TipoUbicacionDTO.desdeModelo(ubicacion.getTipo()),
                ubicacion.getNombre(),
                ubicacion.getFlujoEnergia(),
                ubicacion.getConexiones(),
                ubicacion.getVertices().stream()
                        .map(CoordenadasDTO::desdeModelo)
                        .collect(Collectors.toSet()),
                ubicacion.getTemperatura()
        );
    }
}
