// src/app/pages/lista-ordenes/lista-ordenes.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { OrdenService } from '../../services/orden.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-lista-ordenes',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  templateUrl: './lista-ordenes.component.html',
  styleUrl: './lista-ordenes.component.css'
})
export class ListaOrdenesComponent implements OnInit {
  ordenes: any[] = [];
  cargando: boolean = true;

  constructor(private ordenService: OrdenService) {}

  ngOnInit(): void {
    this.cargarOrdenes();
  }

  cargarOrdenes(): void {
    this.cargando = true;
    this.ordenService.listarOrdenes().subscribe({
      next: (data) => {
        // Filtramos para mostrar solo las órdenes activas,
        // asumiendo que el backend envía todas o ya las filtra.
        this.ordenes = data.filter(orden => orden.isActive !== false);
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar las órdenes', err);
        Swal.fire('Error', 'No se pudieron cargar las órdenes de trabajo', 'error');
        this.cargando = false;
      }
    });
  }

  eliminarOrden(id: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: "Esta orden se marcará como inactiva (eliminación lógica).",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.ordenService.eliminarOrdenLogica(id).subscribe({
          next: () => {
            Swal.fire('Eliminada!', 'La orden ha sido dada de baja.', 'success');
            this.cargarOrdenes(); // Recargamos la lista
          },
          error: () => Swal.fire('Error', 'No se pudo eliminar la orden', 'error')
        });
      }
    });
  }
}
