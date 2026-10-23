package com.ordenes_back.controller;

import com.ordenes_back.dto.OrdenRequest;
import com.ordenes_back.model.Ordenes;
import com.ordenes_back.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// El controlador ya no toca el repositorio: solo recibe la peticion,
// llama al servicio y devuelve la respuesta (responsabilidad unica).
// El CORS ya esta en SecurityConfig, por eso quitamos @CrossOrigin.
@RestController
@RequestMapping("/api/ordenes")
@Tag(name = "Ordenes de Trabajo", description = "Endpoints protegidos para gestionar las reparaciones")
@SecurityRequirement(name = "bearerAuth")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @Operation(summary = "Listar ordenes", description = "Devuelve todas las ordenes activas")
    @GetMapping
    public ResponseEntity<List<Ordenes>> obtenerTodas() {
        return ResponseEntity.ok(ordenService.listarActivas());
    }

    @Operation(summary = "Obtener orden por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Ordenes> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.obtenerActiva(id));
    }

    @Operation(summary = "Crear orden")
    @PostMapping
    public ResponseEntity<Ordenes> crearOrden(@RequestBody OrdenRequest request) {
        return ResponseEntity.ok(ordenService.crear(request));
    }

    @Operation(summary = "Actualizar orden")
    @PutMapping("/{id}")
    public ResponseEntity<Ordenes> actualizarOrden(@PathVariable Long id, @RequestBody OrdenRequest request) {
        return ResponseEntity.ok(ordenService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar orden", description = "Eliminacion logica (isActive = false)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> eliminarOrden(@PathVariable Long id) {
        ordenService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Orden eliminada correctamente"));
    }

    // --- FILTROS ---

    @Operation(summary = "Buscar por cliente")
    @GetMapping("/buscar/cliente")
    public ResponseEntity<List<Ordenes>> buscarPorCliente(@RequestParam String nombre) {
        return ResponseEntity.ok(ordenService.buscarPorCliente(nombre));
    }

    @Operation(summary = "Buscar por estado")
    @GetMapping("/buscar/estado")
    public ResponseEntity<List<Ordenes>> buscarPorEstado(@RequestParam String estado) {
        return ResponseEntity.ok(ordenService.buscarPorEstado(estado));
    }
}