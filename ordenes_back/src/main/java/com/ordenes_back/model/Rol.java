package com.ordenes_back.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.java.Log;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Log id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
