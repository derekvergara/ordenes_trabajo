package com.ordenes_back.repository;

import com.ordenes_back.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Rol, Long> {
    Rol findByNombre(String nombre);
}
