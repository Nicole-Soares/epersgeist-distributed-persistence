package ar.edu.unq.commons.dto;

public record RespuestaEspirituDTO(
        Long espirituId,
        Long mediumId,
        String respuesta,
        Double hostilidad
) {}
