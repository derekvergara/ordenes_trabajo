package com.ordenes_back.dto;

import com.ordenes_back.model.Rol;

public record RolResponse (Long id, String nombre){
    public static RolResponse desde(Rol r){
        return  new RolResponse(r.getId() , r.getNombre());
    }
}
