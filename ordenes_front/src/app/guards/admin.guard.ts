// src/app/guards/admin.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { RolService } from '../services/rol.service';

// Si alguien que no es ADMIN escribe /admin-usuarios en la barra,
// lo mandamos a la lista de órdenes.
export const adminGuard: CanActivateFn = () => {
  const rolService = inject(RolService);
  const router = inject(Router);

  return rolService.esAdmin() ? true : router.createUrlTree(['/lista-ordenes']);
};