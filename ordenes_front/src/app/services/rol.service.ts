// src/app/services/rol.service.ts
import { Injectable } from '@angular/core';

// ⚠️ Revisa en tu auth.interceptor.ts con qué nombre guardas el token
// (localStorage.getItem('token')). Si es otro, cámbialo aquí.
const TOKEN_KEY = 'token';

// Lee el rol y el correo que vienen DENTRO del token JWT.
// Solo sirve para mostrar u ocultar cosas en pantalla.
// La seguridad real la hace el backend.
@Injectable({ providedIn: 'root' })
export class RolService {

  esAdmin(): boolean {
    const payload = this.leerPayload();
    if (!payload) return false;

    // Token vencido = sin permisos
    if (payload.exp && payload.exp * 1000 < Date.now()) return false;

    const rol = String(payload.rol ?? '').toUpperCase();
    return rol === 'ADMIN' || rol === 'ROLE_ADMIN';
  }

  correoActual(): string | null {
    return this.leerPayload()?.sub ?? null;
  }

  // Un JWT tiene 3 partes separadas por puntos: header.payload.firma
  // El payload es JSON en Base64URL: lo decodificamos para leer "rol" y "sub".
  private leerPayload(): any | null {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) return null;

    try {
      const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      const json = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(json);
    } catch {
      return null;
    }
  }
}