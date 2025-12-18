package ar.edu.unq.epersgeist.controller.dto.medium;

import ar.edu.unq.epersgeist.controller.dto.espiritu.RecuperarEspirituDTO;
import ar.edu.unq.epersgeist.controller.dto.ubicacion.CoordenadasDTO;
import ar.edu.unq.epersgeist.controller.dto.ubicacion.RecuperarUbicacionDTO;
import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.List;
import java.util.stream.Collectors;

public record RecuperarMediumDTO(Long id,
                                 String nombre,
                                 Integer manaMax,
                                 Integer mana,
                                 Long ubicacionId,
                                 List<RecuperarEspirituDTO> espiritus,
                                 RecuperarUbicacionDTO ubicacion,
                                 CoordenadasDTO coordenadas,
                                 Double cordura) {

    public static RecuperarMediumDTO desdeModelo(Medium medium) {
        List<RecuperarEspirituDTO> espiritusDTO = medium.getEspiritus()
                .stream()
                .map(RecuperarEspirituDTO::desdeModelo)
                .collect(Collectors.toList());

        return new RecuperarMediumDTO(
                medium.getId(),
                medium.getNombre(),
                medium.getManaMax(),
                medium.getMana(),
                medium.getUbicacion().getId(),
                espiritusDTO,
                RecuperarUbicacionDTO.desdeModelo(medium.getUbicacion()),
                CoordenadasDTO.desdeModelo(medium.getCoordenadas()),
                medium.getCordura()
        );
    }

}
