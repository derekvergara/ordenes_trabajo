package com.ordenes_back.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class AuthResponse {
    private String token;
    private String rol;
    private String nombre;
    private String correo;
}
