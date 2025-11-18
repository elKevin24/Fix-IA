import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ClienteService, TicketService } from '../../../core/services';
import { Cliente, Ticket } from '../../../shared/models';

@Component({
  selector: 'app-detalle-cliente',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './detalle-cliente.html',
  styleUrl: './detalle-cliente.scss'
})
export class DetalleClienteComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private clienteService = inject(ClienteService);
  private ticketService = inject(TicketService);

  cliente: Cliente | null = null;
  tickets: Ticket[] = [];
  isLoading = false;
  isLoadingTickets = false;
  errorMessage = '';
  clienteId: number = 0;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.clienteId = +id;
      this.loadCliente();
      this.loadTickets();
    } else {
      this.router.navigate(['/clientes']);
    }
  }

  loadCliente(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.clienteService.getById(this.clienteId).subscribe({
      next: (response) => {
        this.cliente = response.data;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al cargar el cliente';
        this.isLoading = false;
      }
    });
  }

  loadTickets(): void {
    this.isLoadingTickets = true;

    this.clienteService.getTickets(this.clienteId, { page: 0, size: 10 }).subscribe({
      next: (response) => {
        this.tickets = response.data.content;
        this.isLoadingTickets = false;
      },
      error: (error) => {
        console.error('Error al cargar tickets:', error);
        this.isLoadingTickets = false;
      }
    });
  }

  eliminarCliente(): void {
    if (!this.cliente) return;

    const confirmMessage = this.cliente.totalTickets > 0
      ? `Este cliente tiene ${this.cliente.totalTickets} ticket(s) asociados. ¿Estás seguro de eliminarlo?`
      : '¿Estás seguro de eliminar este cliente?';

    if (!confirm(confirmMessage)) {
      return;
    }

    this.clienteService.delete(this.clienteId).subscribe({
      next: () => {
        this.router.navigate(['/clientes']);
      },
      error: (error) => {
        alert('Error al eliminar cliente: ' + (error.message || 'Error desconocido'));
      }
    });
  }

  getEstadoBadgeClass(estado: string): string {
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
      month: 'long',
      day: 'numeric'
    });
  }
}
