// src/app/pages/login/login.component.ts
import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  loginForm: ReturnType<FormBuilder['group']>;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      correo: ['', [Validators.required, Validators.email]],
      contrasena: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    const { correo, contrasena } = this.loginForm.value;

    // Pasamos correo y contrasena al servicio adaptado
    this.authService.login(correo!, contrasena!).subscribe({
      next: () => {
        Swal.fire('Bienvenido', 'Has iniciado sesión correctamente', 'success');
        // Redirección corregida hacia tu nueva página de órdenes
        this.router.navigate(['/lista-ordenes']);
      },
      error: () => {
        Swal.fire('Error', 'Credenciales inválidas', 'error');
      }
    });
  }
}