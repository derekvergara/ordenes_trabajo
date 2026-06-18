package com.ordenes_back.repository;

import com.ordenes_back.model.Ordenes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenRepository extends JpaRepository <Ordenes, Long>{

    //listamos todas las ordenes que estan activas en vista general
    List<Ordenes> findByIsActiveTrue();

    //filtramos por el nombre del cliente
    List<Ordenes> findByClienteContainingIgnoreCaseAndIsActiveTrue(String cliente);

    //filtramos por el estado del equipo
    List<Ordenes> findByEstadoAndIsActiveTrue(String estado);

    //filtramos por el equipo
    List<Ordenes> findByEquipoContainingIgnoreCaseAndIsActiveTrue(String equipo);

}
