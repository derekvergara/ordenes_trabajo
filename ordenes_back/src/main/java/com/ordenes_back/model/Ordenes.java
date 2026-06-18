package com.ordenes_back.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "ordenes")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Ordenes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_ingreso",nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    @Column(nullable = false)
    private String cliente;

    @Column
    private String telefono;

    @Column(nullable = false)
    private String equipo;

    @Column(nullable = false)
    private String danio;

    @Column(name = "costo_valor", nullable = false)
    private Double costoValor;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String repuestos;

    @Column(length = 500)
    private String observaciones;

    //implementamos la eliminacion logica
    @Column(name = "is_Active", nullable = false)
    private boolean isActive = true;

}
