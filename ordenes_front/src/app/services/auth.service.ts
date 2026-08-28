// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

interface LoginResponse {
  access_token: string;
  token_type: string;
  usuario: {
    id: string;
    correo: string;
    rol: string;
  };
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // Ajustado al backend local de Spring Boot actual
  private apiSpring = 'http://localhost:8080/api/auth';  
  private tokenKey = 'token';

  constructor(private http: HttpClient, private router: Router) {}

  login(correo: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiSpring}/login`, { correo, password })
      .pipe(
        tap((res) => {
          // Asegúrate de que "res.token" coincide con lo que devuelve tu backend
          localStorage.setItem(this.tokenKey, res.token);  
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