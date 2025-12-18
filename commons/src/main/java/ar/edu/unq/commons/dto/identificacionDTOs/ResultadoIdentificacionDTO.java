package ar.edu.unq.commons.dto.identificacionDTOs;

public record ResultadoIdentificacionDTO(
        boolean exito,
        boolean muere,
        String mensaje,
        Long espirituId,
        Long mediumId
) {}