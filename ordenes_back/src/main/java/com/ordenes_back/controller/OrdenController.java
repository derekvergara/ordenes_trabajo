package com.ordenes_back.controller;

import com.ordenes_back.dto.OrdenRequest;
import com.ordenes_back.model.Ordenes;
import com.ordenes_back.repository.OrdenRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Ordenes de Trabajo", description = "Endpoints protegidos para gestionar las reparaciones")
@SecurityRequirement(name = "bearerAuth")
public class OrdenController {
    @Autowired
    private OrdenRepository ordenRepository;

    @Operation(summary = "Listar ordenes", description = "Devuelve todas las ordenes activas")
    @GetMapping
    public ResponseEntity<List<Ordenes>> obtenerTodas() {
        // Usamos el método que ignora las eliminadas lógicamente
        return ResponseEntity.ok(ordenRepository.findByIsActiveTrue());
    }

    @Operation(summary = "Obtener orden por ID", description = "Devuelve una orden específica por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Ordenes> orden = ordenRepository.findById(id);
        if (orden.isPresent() && orden.get().isActive()) {
            return ResponseEntity.ok(orden.get());
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear orden", description = "Crea una nueva orden de trabajo")
    @PostMapping
    public ResponseEntity<?> crearOrden(@RequestBody OrdenRequest request) {
        Ordenes orden = new Ordenes();
        orden.setFechaIngreso(request.getFechaIngreso());
        orden.setFechaSalida(request.getFechaSalida());
        orden.setCliente(request.getCliente());
        orden.setTelefono(request.getTelefono());
        orden.setEquipo(request.getEquipo());
        orden.setDanio(request.getDanio());
        orden.setCostoValor(request.getCostoValor());
        orden.setEstado(request.getEstado());
        orden.setRepuestos(request.getRepuestos());
        orden.setObservaciones(request.getObservaciones());
        // isActive ya es true por defecto en el modelo

        Ordenes guardada = ordenRepository.save(orden);
        return ResponseEntity.ok(guardada);
    }

    @Operation(summary = "Actualizar orden", description = "Modifica los datos de una orden existente")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarOrden(@PathVariable Long id, @RequestBody OrdenRequest request) {
        Optional<Ordenes> optOrden = ordenRepository.findById(id);

        if (optOrden.isPresent() && optOrden.get().isActive()) {
            Ordenes orden = optOrden.get();
            orden.setFechaIngreso(request.getFechaIngreso());
            orden.setFechaSalida(request.getFechaSalida());
            orden.setCliente(request.getCliente());
            orden.setTelefono(request.getTelefono());
            orden.setEquipo(request.getEquipo());
            orden.setDanio(request.getDanio());
            orden.setCostoValor(request.getCostoValor());
            orden.setEstado(request.getEstado());
            orden.setRepuestos(request.getRepuestos());
            orden.setObservaciones(request.getObservaciones());

            return ResponseEntity.ok(ordenRepository.save(orden));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar orden", description = "Cambia el estado isActive a false en lugar de borrarla")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // asignamos que el admin pueda eliminar las ordenes
    public ResponseEntity<?> eliminarOrden(@PathVariable Long id) {
        Optional<Ordenes> optOrden = ordenRepository.findById(id);

        if (optOrden.isPresent()) {
            Ordenes orden = optOrden.get();
            orden.setActive(false); // eliminacion logica
            ordenRepository.save(orden);
            return ResponseEntity.ok().body("{\"mensaje\": \"Orden eliminada correctamente\"}");
        }
        return ResponseEntity.notFound().build();
    }

    // --- ENDPOINTS DE FILTRADO ---

    @Operation(summary = "Buscar por cliente", description = "Filtra ordenes por nombre del cliente")
    @GetMapping("/buscar/cliente")
    public ResponseEntity<List<Ordenes>> buscarPorCliente(@RequestParam String nombre) {
        return ResponseEntity.ok(ordenRepository.findByClienteContainingIgnoreCaseAndIsActiveTrue(nombre));
    }

    @Operation(summary = "Buscar por estado", description = "Filtra ordenes por estado (ej. PENDIENTE, FINALIZADO)")
    @GetMapping("/buscar/estado")
    public ResponseEntity<List<Ordenes>> buscarPorEstado(@RequestParam String estado) {
        return ResponseEntity.ok(ordenRepository.findByEstadoAndIsActiveTrue(estado));
    }
}
