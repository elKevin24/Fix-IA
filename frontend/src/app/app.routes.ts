import { Routes } from '@angular/router';
import { authGuard } from './core/guards';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/consulta',
    pathMatch: 'full'
  },
  {
    path: 'consulta',
    loadComponent: () => import('./features/public/consulta-ticket/consulta-ticket').then(m => m.ConsultaTicketComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.LoginComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard').then(m => m.DashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'clientes',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/clientes/lista-clientes/lista-clientes').then(m => m.ListaClientesComponent)
      },
      {
        path: 'nuevo',
        loadComponent: () => import('./features/clientes/form-cliente/form-cliente').then(m => m.FormClienteComponent)
      },
      {
        path: ':id',
        loadComponent: () => import('./features/clientes/detalle-cliente/detalle-cliente').then(m => m.DetalleClienteComponent)
      },
      {
        path: ':id/editar',
        loadComponent: () => import('./features/clientes/form-cliente/form-cliente').then(m => m.FormClienteComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/consulta'
  }
];
