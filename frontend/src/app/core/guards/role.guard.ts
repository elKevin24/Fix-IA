import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Rol } from '../../shared/models';

/**
 * Factory para crear guards de rol.
 *
 * Permite crear guards que verifican si el usuario tiene uno o más roles específicos.
 *
 * Ejemplo de uso en las rutas:
 * ```
 * {
 *   path: 'admin',
 *   canActivate: [roleGuard([Rol.ADMINISTRADOR])]
 * }
 * ```
 */
export function roleGuard(allowedRoles: Rol[]): CanActivateFn {
  return (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    // Verificar si está autenticado
    if (!authService.isAuthenticated()) {
      router.navigate(['/login'], {
        queryParams: { returnUrl: state.url }
      });
      return false;
    }

    // Verificar si tiene alguno de los roles permitidos
    if (authService.hasAnyRole(allowedRoles)) {
      return true;
    }

    // Si no tiene permisos, redirigir al dashboard
    console.warn('Acceso denegado: rol no autorizado');
    router.navigate(['/dashboard']);
    return false;
  };
}

/**
 * Guards predefinidos para cada rol
 */
export const adminGuard: CanActivateFn = roleGuard([Rol.ADMINISTRADOR]);

export const recepcionistaGuard: CanActivateFn = roleGuard([
  Rol.ADMINISTRADOR,
  Rol.RECEPCIONISTA
]);

export const tecnicoGuard: CanActivateFn = roleGuard([
  Rol.ADMINISTRADOR,
  Rol.RECEPCIONISTA,
  Rol.TECNICO
]);
