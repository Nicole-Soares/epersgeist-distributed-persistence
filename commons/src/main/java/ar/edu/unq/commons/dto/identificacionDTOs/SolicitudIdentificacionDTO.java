package ar.edu.unq.commons.dto.identificacionDTOs;

public record SolicitudIdentificacionDTO(
        Long mediumId,
        double corduraNormalizada,
        Real real,
        MediumConjetura mediumConjetura
) {
    public record Real(
            Long espirituId,
            String nombre,
            String tipo,
            double hostilidadNormalizada
    ) { }

    public record MediumConjetura(
            String nombre
    ) { }
}