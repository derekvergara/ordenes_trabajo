package com.ordenes_back.dto;

import com.ordenes_back.model.Usuario;

public record UsuarioAdminResponse (
         Long id,
         String nombre,
         String correo,
         Long rolId,
         String rolNombre,
         boolean activo
) {
    public static UsuarioAdminResponse desde(Usuario u){
        return new UsuarioAdminResponse(
                u.getId(),
                u.getNombre(),
                u.getCorreo(),
                u.getRol()  != null ? u.getRol().getId() : null,
                u.getRol() != null ? u.getRol().getNombre(): null,
                u.isActive()

        );
    }
}
