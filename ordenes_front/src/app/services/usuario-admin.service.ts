// src/app/services/usuario-admin.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UsuarioAdmin {
  id: number;
  nombre: string;
  correo: string;
  rolId: number;
  rolNombre: string;
  activo: boolean;
}

export interface Rol {
  id: number;
  nombre: string;
}

export interface UsuarioAdminUpdate {
  nombre: string;
  password?: string; // opcional: solo se envía si se escribió una nueva
  rolId: number;
  activo: boolean;
}

@Injectable({ providedIn: 'root' })
export class UsuarioAdminService {
  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  listarUsuarios(): Observable<UsuarioAdmin[]> {
    return this.http.get<UsuarioAdmin[]>(`${this.apiUrl}/usuarios`);
  }

  listarRoles(): Observable<Rol[]> {
    return this.http.get<Rol[]>(`${this.apiUrl}/roles`);
  }

  actualizarUsuario(id: number, datos: UsuarioAdminUpdate): Observable<UsuarioAdmin> {
    return this.http.put<UsuarioAdmin>(`${this.apiUrl}/usuarios/${id}`, datos);
  }
}