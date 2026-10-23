// src/app/services/orden.service.ts
import { Injectable } from '@angular/core';
import { HttpClient , HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrdenService {
  private apiUrl = 'http://localhost:8080/api/ordenes'; // URL de tu backend

  constructor(private http: HttpClient) {}

  listarOrdenes(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  obtenerOrden(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  crearOrden(orden: any): Observable<any> {
    // Por defecto, al crear, la orden está activa
    orden.isActive = true; 
    return this.http.post<any>(this.apiUrl, orden);
  }

  actualizarOrden(id: number, orden: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, orden);
  }

  // Eliminación lógica (cambiar estado en lugar de borrar de BD)
  eliminarOrdenLogica(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }

  //Implementacion de los filtros de busqueda
  buscarPorCliente(nombre: string): Observable<any[]>{
    const params = new HttpParams().set('nombre', nombre);
    return this.http.get<any[]>(`${this.apiUrl}/buscar/cliente`, { params });
  }

  buscarPorEstado(estado: string): Observable<any[]>{
    const params = new HttpParams().set('estado', estado);
    return this.http.get<any[]>(`${this.apiUrl}/buscar/estado`, { params });
  }

}
