package ar.edu.unq.servicio_mensajeria.controller;


public record ComunicacionActivaDTO(
        Long mediumId,
        Long ubicacionId,
        Long espirituId
) {}
