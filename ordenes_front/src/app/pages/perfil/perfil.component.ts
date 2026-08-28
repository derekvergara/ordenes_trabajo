// src/app/pages/perfil/perfil.component.ts
import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [NavbarComponent, CommonModule],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css'
})
export class PerfilComponent implements OnInit {
  usuario: any;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    // 🚀 Magia del Interceptor: Solo hacemos el GET a la URL local de tu backend.
    // El interceptor se encarga de enviar el token por detrás automáticamente.
    this.http.get('http://localhost:8080/api/user/perfil').subscribe({
      next: (data) => this.usuario = data,
      error: (err) => console.error('Error al cargar perfil:', err)
    });
  }
}