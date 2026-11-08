package org.utn.tpfinalprogramacion3.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Mismas reglas que frontend/src/app/shared/validadores.ts
public record RegisterRequest(
        @NotBlank(message = "Ingresá un nombre.")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres.")
        String nombre,

        @Min(value = 1, message = "La edad debe ser mayor que cero.")
        int edad,

        @NotBlank(message = "Ingresá tu email.")
        @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
        @Email(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$", message = "Ingresá un email válido (ej. nombre@correo.com).")
        String email,

        @NotBlank(message = "Ingresá una contraseña.")
        @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres.")
        @Pattern(regexp = "^(?=.*\\p{L})(?=.*\\d).*$", message = "La contraseña debe tener al menos una letra y un número.")
        String password
) {}
