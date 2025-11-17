import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Interceptor HTTP para manejo centralizado de errores.
 *
 * Captura errores HTTP y los maneja apropiadamente:
 * - 401: Sesión expirada, redirige al login
 * - 403: Sin permisos
 * - 404: Recurso no encontrado
 * - 500: Error del servidor
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Ocurrió un error inesperado';

      if (error.error instanceof ErrorEvent) {
        // Error del lado del cliente
        errorMessage = `Error: ${error.error.message}`;
      } else {
        // Error del lado del servidor
        switch (error.status) {
          case 401:
            // No autorizado - sesión expirada
            errorMessage = 'Sesión expirada. Por favor, inicia sesión nuevamente.';
            authService.logout();
            break;

          case 403:
            // Sin permisos
            errorMessage = 'No tienes permisos para realizar esta acción.';
            break;

          case 404:
            // No encontrado
            errorMessage = 'El recurso solicitado no fue encontrado.';
            break;

          case 400:
            // Bad Request - puede tener mensaje personalizado del backend
            if (error.error?.message) {
              errorMessage = error.error.message;
            } else {
              errorMessage = 'La solicitud contiene datos inválidos.';
            }
            break;

          case 500:
          case 502:
          case 503:
            // Error del servidor
            errorMessage = 'Error del servidor. Por favor, intenta más tarde.';
            break;

          default:
            if (error.error?.message) {
              errorMessage = error.error.message;
            } else if (error.message) {
              errorMessage = error.message;
            }
        }
      }

      // Loguear el error en consola para debugging
      console.error('HTTP Error:', {
        status: error.status,
        message: errorMessage,
        url: req.url,
        error: error.error
      });

      // Retornar el error con el mensaje procesado
      return throwError(() => ({
        status: error.status,
        message: errorMessage,
        originalError: error
      }));
    })
  );
};
