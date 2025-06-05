package org.utn.tpfinalprogramacion3.security.dto;

public record RegisterRequest(
        String nombre,
        int edad,
        String email,
        String password
) {}

