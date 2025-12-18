package ar.edu.unq.epersgeist.controller.dto.medium;

import jakarta.validation.constraints.NotBlank;

public record EnviarMensajeMediumDTO(
        @NotBlank String mensaje
) {}
