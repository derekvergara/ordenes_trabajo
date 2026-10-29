// src/app/pages/admin-usuarios/admin-usuarios.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { RolService } from '../../services/rol.service';
import {
  Rol,
  UsuarioAdmin,
  UsuarioAdminService,
  UsuarioAdminUpdate
} from '../../services/usuario-admin.service';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NavbarComponent],
  templateUrl: './admin-usuarios.component.html'
})
export class AdminUsuariosComponent implements OnInit {
  usuarios: UsuarioAdmin[] = [];
  roles: Rol[] = [];
  cargando = true;
  guardando = false;

  seleccionado: UsuarioAdmin | null = null;
  miCorreo: string | null;
  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private usuarioAdminService: UsuarioAdminService,
    rolService: RolService
  ) {
    this.miCorreo = rolService.correoActual();

    this.form = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      password: ['', [Validators.minLength(6)]], // vacía = no se cambia
      rolId: [null, Validators.required],
      activo: [true]
    });
  }

  ngOnInit(): void {
    this.cargarUsuarios();
    this.usuarioAdminService.listarRoles().subscribe({
      next: (roles) => this.roles = roles,
      error: () => Swal.fire('Error', 'No se pudieron cargar los roles', 'error')
    });
  }

  cargarUsuarios(): void {
    this.cargando = true;
    this.usuarioAdminService.listarUsuarios().subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
        Swal.fire('Error', 'No se pudieron cargar los usuarios', 'error');
      }
    });
  }

  esYo(u: UsuarioAdmin): boolean {
    return !!this.miCorreo && u.correo.toLowerCase() === this.miCorreo.toLowerCase();
  }

  editar(u: UsuarioAdmin): void {
    this.seleccionado = u;
    this.form.reset({
      nombre: u.nombre,
      password: '',
      rolId: u.rolId,
      activo: u.activo
    });

    // Un admin no puede quitarse su propio rol ni desactivarse
    // (el backend también lo bloquea, esto es solo para la pantalla)
    if (this.esYo(u)) {
      this.form.get('rolId')?.disable();
      this.form.get('activo')?.disable();
    } else {
      this.form.get('rolId')?.enable();
      this.form.get('activo')?.enable();
    }

    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelar(): void {
    this.seleccionado = null;
    this.form.reset();
  }

  guardar(): void {
    if (!this.seleccionado || this.form.invalid) {
      Swal.fire('Atención', 'Revisa los campos del formulario', 'warning');
      return;
    }

    // getRawValue() incluye los campos deshabilitados (¿recuerdas el bug de la fecha?)
    const valores = this.form.getRawValue();
    const datos: UsuarioAdminUpdate = {
      nombre: valores.nombre,
      rolId: Number(valores.rolId),
      activo: valores.activo
    };
    if (valores.password) {
      datos.password = valores.password;
    }

    const cambioRol = datos.rolId !== this.seleccionado.rolId;

    this.guardando = true;
    this.usuarioAdminService.actualizarUsuario(this.seleccionado.id, datos).subscribe({
      next: () => {
        this.guardando = false;
        Swal.fire(
          '¡Listo!',
          cambioRol
            ? 'Usuario actualizado. El nuevo rol se aplicará cuando el usuario vuelva a iniciar sesión.'
            : 'Usuario actualizado correctamente',
          'success'
        );
        this.cancelar();
        this.cargarUsuarios();
      },
      error: (err) => {
        this.guardando = false;
        // El backend envía el motivo (ej. "No puedes desactivar tu propia cuenta")
        Swal.fire('Error', err.error?.message || 'No se pudo actualizar el usuario', 'error');
      }
    });
  }

  nombreRol(nombre: string): string {
    const n = (nombre || '').toUpperCase().replace('ROLE_', '');
    return n === 'ADMIN' ? 'Administrador' : n === 'USER' ? 'Usuario' : nombre;
  }
}