// src/app/shared/navbar/navbar.component.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router , RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  private router = inject(Router);
  private auth = inject(AuthService);

  cerrarSesion() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  estaLogueado(): boolean {
    return this.auth.isAuthenticated();
  }
}