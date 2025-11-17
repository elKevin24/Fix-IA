import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard de autenticación.
 *
 * Protege rutas que requieren que el usuario esté autenticado.
 * Si el usuario no está autenticado, redirige al login.
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  // Redirigir al login guardando la URL intentada
  router.navigate(['/login'], {
    queryParams: { returnUrl: state.url }
  });

  return false;
};
