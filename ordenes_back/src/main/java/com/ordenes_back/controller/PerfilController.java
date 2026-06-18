package com.ordenes_back.controller;

import com.ordenes_back.model.Usuario;
import com.ordenes_back.repository.UserRepository;
import com.ordenes_back.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@Tag(name = "Perfil de Usuario", description = "Endpoints protegidos para la gestion del perfil del usuario verificado")
public class PerfilController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Obtener perfil", description = "devolvemos los datos del usuario logueado usando su token JWT")
    @GetMapping("/perfil")
    @SecurityRequirement(name = "bearerAuth") // Exige token en Swagger
    public ResponseEntity<?> getPerfil(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extraer el token y el correo
            String token = authHeader.replace("Bearer ", "");
            String correo = jwtUtil.extractUsername(token);

            //  metodo para ver si el usuario esta activo
            Optional<Usuario> usuarioOpt = userRepository.findByCorreoAndIsActiveTrue(correo);

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                return ResponseEntity.ok(Map.of(
                        "id", usuario.getId(),
                        "nombre", usuario.getNombre(),
                        "correo", usuario.getCorreo(),
                        "rol", usuario.getRol().getNombre()
                ));
            } else {
                return ResponseEntity.status(404).body("Usuario no encontrado o cuenta inactiva");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token invalido o expirado");
        }
    }
}