package com.ordenes_back.service;

import com.ordenes_back.dto.RolResponse;
import com.ordenes_back.dto.UsuarioAdminResponse;
import com.ordenes_back.dto.UsuarioAdminUpdateRequest;
import com.ordenes_back.model.AccionAuditoria;
import com.ordenes_back.model.EntidadAuditoria;
import com.ordenes_back.model.Rol;
import com.ordenes_back.model.Usuario;
import com.ordenes_back.repository.RoleRepository;
import com.ordenes_back.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioAdminService {
    public static  final int PASSWORD_MIN = 6;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioAdminService(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder,
                               AuditoriaService auditoriaService){
        this.userRepository =userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    private boolean esAdmin(Rol rol){
        return rol != null && ("ADMIN".equalsIgnoreCase(rol.getNombre()) || "ROLE_ADMIN".equalsIgnoreCase(rol.getNombre()));
    }

    // evitamos que quede el sistema sin ningun administrador
    private void verificarQueQuedeOtroAdmin(Long idExcluido){
        long otrosAdmins = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(idExcluido))
                .filter(Usuario::isActive)
                .filter(u -> esAdmin(u.getRol()))
                .count();
        if (otrosAdmins == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Debe quedar al menos un administrador activo");
        }
    }

    // capturamos al usuario para los logs de registro de cambios

    private Map<String, Object> capturar(Usuario u){
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("nombre", u.getNombre());
        datos.put("rol", u.getRol() != null ? u.getRol().getNombre() : null);
        datos.put("activo", u.isActive());
        return datos;
    }

    //vemos todos los usuairos y el admin puede reactivarlos
    public  List<UsuarioAdminResponse> listar(){
        return userRepository.findAll().stream()
                .map(UsuarioAdminResponse::desde)
                .toList();
    }

    public List<RolResponse> listarRoles(){
        return roleRepository.findAll().stream()
                .map(RolResponse::desde)
                .toList();
    }

    @Transactional
    public UsuarioAdminResponse actualizar(Long id, UsuarioAdminUpdateRequest req, String correoAdmin){
        Usuario usuario =userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        Map<String, Object> antes = capturar(usuario);
        boolean esElMismoAdmin =usuario.getCorreo().equalsIgnoreCase(correoAdmin);

        //nombre
        if (req.nombre() != null && !req.nombre().isBlank()){
            usuario.setNombre(req.nombre().trim());
        }

        // se cambia solo si escribieron una nueva en caso se guarda encriptada con BCrypt
        boolean passwordCambiada = false;
        if (req.password() != null && !req.password().isBlank()){
            if (req.password().length() < PASSWORD_MIN){
                throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, " La contraseña debe tener minimo" + PASSWORD_MIN+ " caracteres");
            }
            usuario.setPassword(passwordEncoder.encode(req.password()));
            passwordCambiada = true;
        }
        // rol del usuario
        if (req.rolId() != null && !req.rolId().equals( usuario.getRol().getId())){
            Rol nuevoRol = roleRepository.findById(req.rolId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol no existe"));
            if( esElMismoAdmin && !esAdmin(nuevoRol)){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No puedes quitarte tu propio rol de administrador");
            }
            if (esAdmin(usuario.getRol()) && !esAdmin(nuevoRol)){
                verificarQueQuedeOtroAdmin(usuario.getId());
            }
            usuario.setRol(nuevoRol);

        }

        if (req.activo() != null && req.activo() != usuario.isActive()){
            if (esElMismoAdmin && !req.activo()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes desactivar tu propia cuenta");
            }
            if (!req.activo() && esAdmin(usuario.getRol())){
                verificarQueQuedeOtroAdmin(usuario.getId());
            }
            usuario.setActive(req.activo());
        }

        Usuario guardado = userRepository.save(usuario);
        //se guarda la contraseña en caso de que se alla cambiado
        String cambios = auditoriaService.detectarCambios(antes, capturar(guardado));
        if (passwordCambiada){
            cambios = (cambios + " password: cambiada;").trim();
        }
        if (!cambios.isEmpty()){
            auditoriaService.registrar(EntidadAuditoria.USUARIO, id , AccionAuditoria.ACTUALIZAR, cambios);
        }
        return UsuarioAdminResponse.desde(guardado);
    }
}
