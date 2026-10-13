package org.utn.tpfinalprogramacion3.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarPerfilRequest(
        @NotBlank @Size(max = 50) String nombre,
        @NotBlank @Email @Size(max = 100) String email,
        @NotNull @Min(1) Integer edad
) {
}
