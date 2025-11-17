import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Interceptor HTTP para agregar el token JWT a las peticiones.
 *
 * Este interceptor agrega automáticamente el header Authorization
 * con el token JWT a todas las peticiones HTTP, excepto las públicas.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  // URLs públicas que no requieren autenticación
  const publicUrls = [
    '/auth/login',
    '/publico/'
  ];

  // Verificar si la URL es pública
  const isPublicUrl = publicUrls.some(url => req.url.includes(url));

  // Si es URL pública, continuar sin modificar
  if (isPublicUrl) {
    return next(req);
  }

  // Obtener el token
  const token = authService.getToken();

  // Si hay token, clonar la petición y agregar el header
  if (token) {
    const clonedReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(clonedReq);
  }

  // Si no hay token, continuar sin modificar
  return next(req);
};
