package com.ordenes_back.controller;

import com.ordenes_back.dto.RolResponse;
import com.ordenes_back.dto.UsuarioAdminResponse;
import com.ordenes_back.dto.UsuarioAdminUpdateRequest;
import com.ordenes_back.service.UsuarioAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Administracion de Usuarios", description = "Solo Admin: editar nombre,contraseña, rol y estado")
@SecurityRequirement(name = "bearerAuth")

public class AdminUsuarioController {

    private final UsuarioAdminService usuarioAdminService;

    public AdminUsuarioController(UsuarioAdminService usuarioAdminService){
        this.usuarioAdminService =usuarioAdminService;
    }

    @Operation(summary = "Listar Usuarios", description = "Incluye activos y inactivos")
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioAdminResponse>> listarUsuarios(){
        return ResponseEntity.ok(usuarioAdminService.listar());
    }

    @Operation(summary = "Listar roles disponibles")
    @GetMapping("/roles")
    public ResponseEntity<List<RolResponse>> listarRoles() {
        return ResponseEntity.ok(usuarioAdminService.listarRoles());
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioAdminResponse> actualizarUsuario(@PathVariable Long id,
                                                                  @RequestBody UsuarioAdminUpdateRequest request,
                                                                  Authentication auth) {
        return ResponseEntity.ok(usuarioAdminService.actualizar(id, request, auth.getName()));
    }
}
