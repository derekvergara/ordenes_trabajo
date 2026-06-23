// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

interface LoginResponse {
  token: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // Ajustado a la URL de tu entorno local de Spring Boot que probamos en Swagger
  private apiSpring = 'http://localhost:8080/api/auth'; 
  private tokenKey = 'token';

  constructor(private http: HttpClient, private router: Router) {}

  login(correo: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiSpring}/login`, { correo, password })
      .pipe(
        tap((res) => {
          localStorage.setItem(this.tokenKey, res.token);  // Guarda el token JWT
        })
      );
  }

  register(nombre: string, correo: string, password: string, rol: string) {
    return this.http.post(`${this.apiSpring}/register`, {
      nombre,
      correo,
      password,
      rol
    });
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.router.navigate(['/login']);
  }

  getUserRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.rol;
    } catch {
      return null;
    }
  }
}