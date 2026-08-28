import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';
import { Router, RouterModule } from '@angular/router';
import { Component } from '@angular/core';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './registro.component.html',
  styleUrl: './registro.component.css'
})
export class RegistroComponent {
  registroForm: FormGroup;
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registroForm = this.fb.group({
      nombre: ['', Validators.required],
      correo: ['', [Validators.required, Validators.email]],
      contrasena: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.registroForm.invalid) return;

    const { nombre, correo, contrasena } = this.registroForm.value;
    const rolPorDefecto = 'USER'; // Asignación automática

    this.authService.register(nombre, correo, contrasena, rolPorDefecto).subscribe({
      next: () => {
        Swal.fire('¡Éxito!', 'Cuenta creada correctamente. Ahora puedes iniciar sesión.', 'success');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error(err);
        Swal.fire('Error', 'Hubo un problema al registrar la cuenta', 'error');
      }
    });
  }
}
