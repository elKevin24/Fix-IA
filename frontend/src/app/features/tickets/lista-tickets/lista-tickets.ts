import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { TicketService, AuthService } from '../../../core/services';
import { Ticket, EstadoTicket, Usuario, Rol } from '../../../shared/models';

@Component({
  selector: 'app-lista-tickets',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './lista-tickets.html',
  styleUrl: './lista-tickets.scss'
})
export class ListaTicketsComponent implements OnInit {
  private ticketService = inject(TicketService);
  private authService = inject(AuthService);
  private router = inject(Router);

  tickets: Ticket[] = [];
  searchControl = new FormControl('');
  estadoFilterControl = new FormControl('');
  isLoading = false;
  errorMessage = '';

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  // User info
  currentUser: Usuario | null = null;

  // Estados disponibles
  estados = Object.values(EstadoTicket);

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadTickets();

    // Search con debounce
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(searchTerm => {
        this.currentPage = 0;
        this.loadTickets();
      });

    // Filtro por estado
    this.estadoFilterControl.valueChanges.subscribe(() => {
      this.currentPage = 0;
      this.loadTickets();
    });
  }

  loadTickets(): void {
    this.isLoading = true;
    this.errorMessage = '';

    const estado = this.estadoFilterControl.value;
    const searchTerm = this.searchControl.value?.trim();

    // Si hay filtro por estado
    if (estado) {
      this.ticketService.getByEstado(estado as EstadoTicket, { page: this.currentPage, size: this.pageSize })
        .subscribe({
          next: (response) => {
            this.tickets = response.data.content;
            this.totalElements = response.data.totalElements;
            this.totalPages = response.data.totalPages;
            this.isLoading = false;
          },
          error: (error) => {
            this.errorMessage = error.message || 'Error al cargar tickets';
            this.isLoading = false;
          }
        });
    }
    // Si es técnico, cargar solo sus tickets
    else if (this.currentUser?.rol === Rol.TECNICO) {
      this.ticketService.getByTecnico(this.currentUser.id, { page: this.currentPage, size: this.pageSize })
        .subscribe({
          next: (response) => {
            this.tickets = response.data.content;
            this.totalElements = response.data.totalElements;
            this.totalPages = response.data.totalPages;
            this.isLoading = false;
          },
          error: (error) => {
            this.errorMessage = error.message || 'Error al cargar tickets';
            this.isLoading = false;
          }
        });
    }
    // Cargar todos los tickets
    else {
      this.ticketService.getAll({ page: this.currentPage, size: this.pageSize })
        .subscribe({
          next: (response) => {
            this.tickets = response.data.content;
            this.totalElements = response.data.totalElements;
            this.totalPages = response.data.totalPages;
            this.isLoading = false;
          },
          error: (error) => {
            this.errorMessage = error.message || 'Error al cargar tickets';
            this.isLoading = false;
          }
        });
    }
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadTickets();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getEstadoBadgeClass(estado: EstadoTicket): string {
    const classes: { [key: string]: string } = {
      'INGRESADO': 'bg-secondary',
      'ASIGNADO': 'bg-info',
      'EN_DIAGNOSTICO': 'bg-primary',
      'PRESUPUESTADO': 'bg-warning text-dark',
      'APROBADO': 'bg-success',
      'RECHAZADO': 'bg-danger',
      'EN_REPARACION': 'bg-primary',
      'EN_PRUEBAS': 'bg-info',
      'LISTO_ENTREGA': 'bg-success',
      'ENTREGADO': 'bg-dark',
      'CANCELADO': 'bg-danger'
    };
    return classes[estado] || 'bg-secondary';
  }

  formatEstado(estado: string): string {
    return estado.replace(/_/g, ' ');
  }

  formatFecha(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleDateString('es-SV', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  getPrioridadBadgeClass(prioridad: string): string {
    const classes: { [key: string]: string } = {
      'BAJA': 'bg-success',
      'MEDIA': 'bg-warning text-dark',
      'ALTA': 'bg-danger',
      'URGENTE': 'bg-danger'
    };
    return classes[prioridad] || 'bg-secondary';
  }

  clearFilters(): void {
    this.searchControl.setValue('');
    this.estadoFilterControl.setValue('');
    this.currentPage = 0;
    this.loadTickets();
  }

  // Helpers para permisos
  canCreateTicket(): boolean {
    return this.authService.hasAnyRole([Rol.ADMINISTRADOR, Rol.RECEPCIONISTA]);
  }

  canViewAllTickets(): boolean {
    return this.authService.hasAnyRole([Rol.ADMINISTRADOR, Rol.RECEPCIONISTA]);
  }
}
