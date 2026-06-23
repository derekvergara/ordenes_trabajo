// src/app/app.config.ts
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
// Importamos withInterceptors
import { provideHttpClient, withInterceptors } from '@angular/common/http'; 
import { provideAnimations } from '@angular/platform-browser/animations';
import { importProvidersFrom } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { routes } from './app.routes';
import { authInterceptor } from './services/auth.interceptor'; // Importamos el interceptor

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes), 
    // Aquí registramos el interceptor
    provideHttpClient(withInterceptors([authInterceptor])), 
    provideAnimations(),
    importProvidersFrom(ReactiveFormsModule)
  ]
};