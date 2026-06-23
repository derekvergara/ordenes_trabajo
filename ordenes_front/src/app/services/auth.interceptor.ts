// src/app/services/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token'); // Busca el token que guardaste en el login

  if (token) {
    // Si hay token, clona la petición y le agrega la cabecera de Authorization
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }

  // Si no hay token (ej. cuando recién se va a loguear), la deja pasar normal
  return next(req);
};