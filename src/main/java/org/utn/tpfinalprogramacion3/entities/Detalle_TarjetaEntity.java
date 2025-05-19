package org.utn.tpfinalprogramacion3.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.Year;

@Entity
@Getter
@Setter
public class Detalle_TarjetaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDetalle;

    @Size(min = 16, max = 16)
    @Column(name = "numero_tarjeta", nullable = false, length = 16)
    private String numeroTarjeta;

    @Min(1)
    @Max(12)
    @Column(name = "mes_vencimiento", nullable = false)
    private int mesVencimiento;

    @Column(name = "anio_vencimiento", nullable = false)
    @Min(value = Year.MIN_VALUE, message = "El año de vencimiento no puede ser menor al año actual")
    private int anioVencimiento;

    @Size(min = 3, max = 3)
    @Column(name = "cvv", nullable = false, length = 3)
    private String cvv;

    @ManyToOne
    @JoinColumn(name = "id_MetodoPago",nullable = false)
    private MetodoDePagoEntity metodoDePago;

}
