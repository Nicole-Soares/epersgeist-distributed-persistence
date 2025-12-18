package ar.edu.unq.epersgeist.controller.dto.espiritu;

import ar.edu.unq.epersgeist.controller.dto.ubicacion.CoordenadasDTO;
import ar.edu.unq.epersgeist.controller.dto.ubicacion.RecuperarUbicacionDTO;
import ar.edu.unq.epersgeist.modelo.espiritu.Angel;
import ar.edu.unq.epersgeist.modelo.espiritu.Espiritu;

public record RecuperarEspirituDTO(Long id, TipoEspirituDTO tipo, String nombre, Integer nivelDeConexion, Long mediumId, Long dominanteId, RecuperarUbicacionDTO ubicacion, CoordenadasDTO coordenadas, Double hostilidad) {
    public static RecuperarEspirituDTO desdeModelo(Espiritu espiritu) {
        return new RecuperarEspirituDTO(
                espiritu.getId(),
                crearTipoDTO(espiritu),
                espiritu.getNombre(),
                espiritu.getNivelDeConexion(),
                espiritu.getMedium() != null ? espiritu.getMedium().getId() : null,
                espiritu.getEspirituDominante() != null ? espiritu.getEspirituDominante().getId() : null,
                RecuperarUbicacionDTO.desdeModelo(espiritu.getUbicacion()),
                CoordenadasDTO.desdeModelo(espiritu.getCoordenadas()),
                espiritu.getHostilidad()
        );
    }

    private static TipoEspirituDTO crearTipoDTO(Espiritu espiritu) {
        if (espiritu instanceof Angel){
            return TipoEspirituDTO.ANGELICAL;
        } else {
            return TipoEspirituDTO.DEMONIO;
        }
    }
}
