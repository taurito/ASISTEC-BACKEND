import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  imports: [RouterLink],
  selector: 'app-dashboard',
  standalone: true,
  styleUrl: './dashboard.css',
  templateUrl: './dashboard.html',
})
export class Dashboard {

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly usuario = this.authService.obtenerUsuario();

  get nombreUsuario(): string {
    return this.usuario?.username ?? 'Usuario';
  }

  get rolPrincipal(): string {
    return this.usuario?.roles
      .map(rol => rol.replace('ROLE_', ''))
      .join(', ') ?? 'Sin rol';
  }

  get puedeConsultarEmpleados(): boolean {
  return this.authService.tieneRol('ADMIN', 'TECNICO');
  }

  cerrarSesion(): void {
    this.authService.cerrarSesion();
    void this.router.navigateByUrl('/login');
  }
}
