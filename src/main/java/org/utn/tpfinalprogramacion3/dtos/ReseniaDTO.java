package org.utn.tpfinalprogramacion3.dtos;

import org.utn.tpfinalprogramacion3.entities.UsuarioEntity;

import java.time.LocalDate;

public class ReseniaDTO {
    private String texto;
    private float calificacion;
    private LocalDate fecha;
    private UsuarioEntity usuario;


}
