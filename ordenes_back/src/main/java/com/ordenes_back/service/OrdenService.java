package com.ordenes_back.service;

import com.ordenes_back.dto.OrdenRequest;
import com.ordenes_back.model.AccionAuditoria;
import com.ordenes_back.model.EntidadAuditoria;
import com.ordenes_back.model.Ordenes;
import com.ordenes_back.repository.OrdenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrdenService {

    private static final String ENTREGADO = "ENTREGADO";

    private final OrdenRepository ordenRepository;
    private final AuditoriaService auditoriaService;

    // Inyeccion por constructor (mejor que @Autowired en el campo: facil de testear y los campos son final)
    public OrdenService(OrdenRepository ordenRepository, AuditoriaService auditoriaService) {
        this.ordenRepository = ordenRepository;
        this.auditoriaService = auditoriaService;
    }

    // ---------- CONSULTAS ----------

    public List<Ordenes> listarActivas() {
        return ordenRepository.findByIsActiveTrue();
    }

    public Ordenes obtenerActiva(Long id) {
        return ordenRepository.findById(id)
                .filter(Ordenes::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
    }

    public List<Ordenes> buscarPorCliente(String nombre) {
        return ordenRepository.findByClienteContainingIgnoreCaseAndIsActiveTrue(nombre);
    }

    public List<Ordenes> buscarPorEstado(String estado) {
        return ordenRepository.findByEstadoAndIsActiveTrue(estado);
    }

    // ---------- ESCRITURA (todas generan log) ----------

    // @Transactional: si falla el log, tampoco se guarda la orden (y al reves).
    // Asi nunca queda un cambio sin su registro de auditoria.
    @Transactional
    public Ordenes crear(OrdenRequest request) {
        Ordenes orden = new Ordenes();
        aplicarDatos(orden, request);
        orden.setFechaIngreso(LocalDate.now()); // la fecha la pone el servidor
        orden.setFechaSalida(ENTREGADO.equals(orden.getEstado()) ? LocalDate.now() : null);
        orden.setActive(true);

        Ordenes guardada = ordenRepository.save(orden);

        auditoriaService.registrar(EntidadAuditoria.ORDEN, guardada.getId(), AccionAuditoria.CREAR,
                "Orden creada - cliente: \"" + guardada.getCliente() + "\", equipo: \"" + guardada.getEquipo() + "\"");
        return guardada;
    }

    @Transactional
    public Ordenes actualizar(Long id, OrdenRequest request) {
        Ordenes orden = obtenerActiva(id);
        Map<String, Object> antes = capturar(orden); // foto ANTES del cambio

        aplicarDatos(orden, request);
        actualizarFechaSalida(orden);

        Ordenes guardada = ordenRepository.save(orden);

        String cambios = auditoriaService.detectarCambios(antes, capturar(guardada)); // foto DESPUES
        if (!cambios.isEmpty()) {
            auditoriaService.registrar(EntidadAuditoria.ORDEN, id, AccionAuditoria.ACTUALIZAR, cambios);
        }
        return guardada;
    }

    @Transactional
    public void eliminar(Long id) {
        Ordenes orden = obtenerActiva(id);
        orden.setActive(false); // eliminacion logica: se oculta, no se borra
        ordenRepository.save(orden);

        auditoriaService.registrar(EntidadAuditoria.ORDEN, id, AccionAuditoria.ELIMINAR,
                "Orden desactivada - cliente: \"" + orden.getCliente() + "\", equipo: \"" + orden.getEquipo() + "\"");
    }

    // ---------- METODOS PRIVADOS ----------

    // Campos que el usuario puede editar desde el formulario (las fechas NO, las maneja el servidor)
    private void aplicarDatos(Ordenes orden, OrdenRequest request) {
        orden.setCliente(request.getCliente());
        orden.setTelefono(request.getTelefono());
        orden.setEquipo(request.getEquipo());
        orden.setDanio(request.getDanio());
        orden.setCostoValor(request.getCostoValor());
        orden.setEstado(request.getEstado());
        orden.setRepuestos(request.getRepuestos() != null ? request.getRepuestos() : "");
        orden.setObservaciones(request.getObservaciones());
    }

    // ENTREGADO -> pone la fecha de hoy (si no tenia). Otro estado -> la quita.
    private void actualizarFechaSalida(Ordenes orden) {
        if (ENTREGADO.equals(orden.getEstado())) {
            if (orden.getFechaSalida() == null) {
                orden.setFechaSalida(LocalDate.now());
            }
        } else {
            orden.setFechaSalida(null);
        }
    }

    // "Foto" de los campos de la orden para comparar antes/despues
    private Map<String, Object> capturar(Ordenes o) {
        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("cliente", o.getCliente());
        datos.put("telefono", o.getTelefono());
        datos.put("equipo", o.getEquipo());
        datos.put("danio", o.getDanio());
        datos.put("costoValor", o.getCostoValor());
        datos.put("estado", o.getEstado());
        datos.put("repuestos", o.getRepuestos());
        datos.put("observaciones", o.getObservaciones());
        datos.put("fechaIngreso", o.getFechaIngreso());
        datos.put("fechaSalida", o.getFechaSalida());
        return datos;
    }
}
