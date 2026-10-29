package com.ordenes_back.dto;

public record UsuarioAdminUpdateRequest (
        String nombre,
        String password,
        Long rolId,
        Boolean activo
) {
}
