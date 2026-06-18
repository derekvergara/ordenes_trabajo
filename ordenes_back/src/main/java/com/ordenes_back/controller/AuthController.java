package com.ordenes_back.controller;

import com.ordenes_back.model.Rol;
import com.ordenes_back.model.Usuario;
import com.ordenes_back.dto.UserRequest;
import com.ordenes_back.repository.RoleRepository;
import com.ordenes_back.repository.UserRepository;
import com.ordenes_back.security.AuthRequest;
import com.ordenes_back.security.AuthResponse;
import com.ordenes_back.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticacion", description = "Endpoints para el registro y login")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Registro de usuarios", description = "Registrar nuevos usuarios")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest request) {
        // verificamos si el correo esta inactivo o desactivado
        if (userRepository.findByCorreo(request.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya se encuentra registrado");
        }

        Rol rol = roleRepository.findByNombre(request.getRol());
        if (rol == null) {
            return ResponseEntity.badRequest().body("Rol inválido");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);

        userRepository.save(usuario);

        // generamos el token para registrar
        String token = jwtUtil.generateToken(usuario.getCorreo(), rol.getNombre());

        // devolvemos el token mas los datos
        AuthResponse response = new AuthResponse(
                token,
                rol.getNombre(),
                usuario.getNombre(),
                usuario.getCorreo()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login de usuarios", description = "Inicia sesion solo si el usuario está ACTIVO y obtiene el token JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // ⚠️ CAMBIO IMPORTANTE: Validamos que el usuario exista Y esté activo
        Optional<Usuario> usuarioOpt = userRepository.findByCorreoAndIsActiveTrue(request.getCorreo());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
                String token = jwtUtil.generateToken(usuario.getCorreo(), usuario.getRol().getNombre());
                return ResponseEntity.ok(new AuthResponse(
                        token,
                        usuario.getRol().getNombre(),
                        usuario.getNombre(),
                        usuario.getCorreo()
                ));
            }
        }

        // mensaje de seguridad con alerta
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales invalidas o cuenta inactiva");
    }
}
