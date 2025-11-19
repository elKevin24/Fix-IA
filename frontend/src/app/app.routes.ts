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
    path: 'tickets',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/tickets/lista-tickets/lista-tickets').then(m => m.ListaTicketsComponent)
      },
      {
        path: 'nuevo',
        loadComponent: () => import('./features/tickets/form-ticket/form-ticket').then(m => m.FormTicketComponent)
      },
      {
        path: ':id',
        loadComponent: () => import('./features/tickets/detalle-ticket/detalle-ticket').then(m => m.DetalleTicketComponent)
      }
    ]
  },
  {
    path: 'inventario',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/inventario/lista-piezas/lista-piezas').then(m => m.ListaPiezasComponent)
      },
      {
        path: 'nuevo',
        loadComponent: () => import('./features/inventario/form-pieza/form-pieza').then(m => m.FormPiezaComponent)
      },
      {
        path: ':id/editar',
        loadComponent: () => import('./features/inventario/form-pieza/form-pieza').then(m => m.FormPiezaComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/consulta'
  }
];
