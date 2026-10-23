package com.ordenes_back.service;
import com.ordenes_back.model.AccionAuditoria;
import com.ordenes_back.model.Auditoria;
import com.ordenes_back.model.EntidadAuditoria;
import com.ordenes_back.repository.AuditoriaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
public class AuditoriaService {
    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository){
        this.auditoriaRepository =auditoriaRepository;
    }
    // Compara dos "fotos" del registro y devuelve solo lo que cambio
    public String detectarCambios(Map<String, Object> antes, Map<String, Object> despues) {
        StringBuilder sb = new StringBuilder();
        for (String campo : antes.keySet()) {
            Object valorAntes = antes.get(campo);
            Object valorDespues = despues.get(campo);
            if (!Objects.equals(valorAntes, valorDespues)) {
                sb.append(campo)
                        .append(": \"").append(valorAntes)
                        .append("\" -> \"").append(valorDespues)
                        .append("\"; ");
            }
        }
        return sb.toString().trim();
    }

    public void registrar(EntidadAuditoria entidad, Long entidadId, AccionAuditoria accion, String cambios) {
        Auditoria log = Auditoria.builder()
                .entidad(entidad)
                .entidad_id(entidadId)
                .accion(accion)
                .usuario(usuarioActual())
                .fecha(LocalDateTime.now())
                .cambios(cambios)
                .build();
        auditoriaRepository.save(log);
    }

    // El JwtFilter guarda el correo como "username" en el SecurityContext
    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "SISTEMA"; // por ejemplo, un registro publico de usuario
        }
        return auth.getName();
    }
}
