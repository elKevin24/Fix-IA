import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TicketService } from '../../../core/services';
import { Ticket, EstadoTicket } from '../../../shared/models';

@Component({
  selector: 'app-consulta-ticket',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './consulta-ticket.html',
  styleUrl: './consulta-ticket.scss'
})
export class ConsultaTicketComponent {
  private fb = inject(FormBuilder);
  private ticketService = inject(TicketService);

  consultaForm: FormGroup;
  ticket: Ticket | null = null;
  errorMessage = '';
  isLoading = false;
  EstadoTicket = EstadoTicket;

  constructor() {
    this.consultaForm = this.fb.group({
      numeroTicket: ['', [Validators.required, Validators.pattern(/^TKT-\d{8}-\d{4}$/)]]
    });
  }

  onConsultar(): void {
    if (this.consultaForm.invalid) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.ticket = null;

    const numeroTicket = this.consultaForm.value.numeroTicket.trim();

    this.ticketService.consultaPublica(numeroTicket).subscribe({
      next: (response) => {
        this.ticket = response.data;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.message || 'Ticket no encontrado';
        this.isLoading = false;
      }
    });
  }

  descargarPDF(): void {
    if (!this.ticket) return;

    this.ticketService.descargarPDFPublico(this.ticket.numeroTicket).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `Ticket-${this.ticket!.numeroTicket}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        alert('Error al descargar el PDF: ' + (error.message || 'Error desconocido'));
      }
    });
  }

  getEstadoBadgeClass(estado: EstadoTicket): string {
    return `badge badge-estado ${estado}`;
  }

  limpiar(): void {
    this.consultaForm.reset();
    this.ticket = null;
    this.errorMessage = '';
  }
}
