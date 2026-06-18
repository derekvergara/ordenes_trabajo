package com.ordenes_back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrdenRequest {
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;

    private String cliente;
    private String telefono;

    private String equipo;
    private String danio;
    private Double costoValor;
    private String estado;
    private String repuestos;

    private String observaciones;
}
