// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { ListaOrdenesComponent } from './pages/lista-ordenes/lista-ordenes.component';
import { FormOrdenComponent } from './pages/form-orden/form-orden.component';
import { PerfilComponent } from './pages/perfil/perfil.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  // Nuevas rutas protegidas para las órdenes
  { path: 'lista-ordenes', component: ListaOrdenesComponent, canActivate: [authGuard] },
  { path: 'form-orden', component: FormOrdenComponent, canActivate: [authGuard] }, // Para crear
  { path: 'form-orden/:id', component: FormOrdenComponent, canActivate: [authGuard] }, // Para editar
  { path: 'perfil', component: PerfilComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' },
];