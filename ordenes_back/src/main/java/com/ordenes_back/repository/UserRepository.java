package com.ordenes_back.repository;

import com.ordenes_back.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, Long> {

    Optional <Usuario> findByCorreo(String correo);

    Optional <Usuario> findByCorreoAndIsActiveTrue(String correo);
}
