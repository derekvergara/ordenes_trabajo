// src/app/pages/lista-ordenes/lista-ordenes.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { OrdenService } from '../../services/orden.service';
import Swal from 'sweetalert2';
import { FormsModule } from '@angular/forms';
import { Observable, of, map, catchError } from 'rxjs';

@Component({
  selector: 'app-lista-ordenes',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, FormsModule],
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
      text: "De eliminar esta orden.",
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

tipoFiltro: 'cliente' | 'estado' | 'id' = 'cliente';
textoBusqueda = '';
estadoSeleccionado = 'INGRESADO';

buscar(): void {
  let peticion: Observable<any[]>;

  if (this.tipoFiltro === 'cliente') {
    const nombre = this.textoBusqueda.trim();
    if (!nombre) {
      this.cargarOrdenes(); // campo vacío = mostrar todas
      return;
    }
    peticion = this.ordenService.buscarPorCliente(nombre);

  } else if (this.tipoFiltro === 'estado') {
    peticion = this.ordenService.buscarPorEstado(this.estadoSeleccionado);

  } else {
    const id = Number(this.textoBusqueda);
    if (!Number.isInteger(id) || id <= 0) {
      Swal.fire('Atención', 'Ingresa un número de orden válido', 'warning');
      return;
    }
    peticion = this.ordenService.obtenerOrden(id).pipe(
      map(orden => [orden]),
      catchError(() => of([]))
    );
  }

  peticion.subscribe({
    next: (resultado) => this.ordenes = resultado,
    error: () => Swal.fire('Error', 'No se pudo realizar la búsqueda', 'error')
  });
}

limpiar(): void {
  this.tipoFiltro = 'cliente';
  this.textoBusqueda = '';
  this.estadoSeleccionado = 'INGRESADO';
  this.cargarOrdenes();
}

cambiarTipo(): void {
  this.textoBusqueda = ''; // busqueda por id
}

}


