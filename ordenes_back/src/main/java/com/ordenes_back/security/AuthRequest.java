package com.ordenes_back.security;

import lombok.Data;

@Data
public class AuthRequest {
    private String correo;
    private String password;
}
