// src/app/pages/form-orden/form-orden.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { OrdenService } from '../../services/orden.service';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-form-orden',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, NavbarComponent],
  templateUrl: './form-orden.component.html',
  styleUrl: './form-orden.component.css'
})
export class FormOrdenComponent implements OnInit {
  ordenForm: FormGroup;
  ordenId: number | null = null;
  esEdicion: boolean = false;

  constructor(
    private fb: FormBuilder,
    private ordenService: OrdenService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.ordenForm = this.fb.group({
      cliente: ['', Validators.required],
      telefono: ['', Validators.required],
      equipo: ['', Validators.required],
      danio: ['', Validators.required],
      estado: ['INGRESADO', Validators.required],
      costoValor: [0, [Validators.required, Validators.min(0)]],
      repuestos: [''],
      observaciones: ['']
    });
  }

  ngOnInit(): void {
    // Verificar si estamos editando una orden existente
    this.ordenId = this.route.snapshot.params['id'];
    if (this.ordenId) {
      this.esEdicion = true;
      this.cargarOrden(this.ordenId);
    }
  }

  cargarOrden(id: number): void {
    this.ordenService.obtenerOrden(id).subscribe({
      next: (orden) => {
        this.ordenForm.patchValue(orden);
      },
      error: () => Swal.fire('Error', 'No se pudo cargar la orden', 'error')
    });
  }

  guardarOrden(): void {
    if (this.ordenForm.invalid) {
      Swal.fire('Atención', 'Por favor llena los campos obligatorios', 'warning');
      return;
    }

    const datosOrden = this.ordenForm.value;

    if (this.esEdicion && this.ordenId) {
      this.ordenService.actualizarOrden(this.ordenId, datosOrden).subscribe({
        next: () => {
          Swal.fire('¡Éxito!', 'Orden actualizada correctamente', 'success');
          this.router.navigate(['/lista-ordenes']);
        },
        error: () => Swal.fire('Error', 'Hubo un problema al actualizar', 'error')
      });
    } else {
      this.ordenService.crearOrden(datosOrden).subscribe({
        next: () => {
          Swal.fire('¡Éxito!', 'Orden creada correctamente', 'success');
          this.router.navigate(['/lista-ordenes']);
        },
        error: () => Swal.fire('Error', 'Hubo un problema al crear la orden', 'error')
      });
    }
  }
}