package com.ordenes_back.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //orden del usuario
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EntidadAuditoria entidad;

    // identificador del registro modificado

    @Column(name = "entidad_id", nullable = false)
    private Long entidad_id;

    // crear,actualizar eliminar

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccionAuditoria accion;

    //correo del propietario que hizo el cambio

    @Column(nullable = false)
    private String usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(columnDefinition = "TEXT")
    private String cambios;
}
