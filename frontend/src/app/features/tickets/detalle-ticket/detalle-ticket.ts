import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TicketService, AuthService } from '../../../core/services';
import { Ticket, Usuario, Rol, EstadoTicket } from '../../../shared/models';

@Component({
  selector: 'app-detalle-ticket',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './detalle-ticket.html',
  styleUrl: './detalle-ticket.scss'
})
export class DetalleTicketComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ticketService = inject(TicketService);
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);

  ticket: Ticket | null = null;
  currentUser: Usuario | null = null;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  ticketId: number = 0;

  // Action forms
  showActionForm = false;
  currentAction: string = '';
  actionForm: FormGroup;

  // Tecnicos disponibles (para asignar)
  tecnicos: Usuario[] = [];

  // PDF download
  isDownloadingPDF = false;

  constructor() {
    this.actionForm = this.fb.group({
      tecnicoId: [null],
      diagnostico: [''],
      costoEstimado: [null],
      tiempoEstimado: [''],
      motivoRechazo: [''],
      piezaId: [null],
      cantidad: [1],
      resultadoPruebas: [''],
      nombreQuienRecibe: [''],
      parentescoRecibe: [''],
      motivoCancelacion: ['']
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.ticketId = +id;
      this.loadTicket();
    } else {
      this.router.navigate(['/tickets']);
    }
  }

  loadTicket(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.ticketService.getById(this.ticketId).subscribe({
      next: (response) => {
        this.ticket = response.data;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al cargar el ticket';
        this.isLoading = false;
      }
    });
  }

  // Workflow actions
  asignarTecnico(): void {
    this.currentAction = 'asignarTecnico';
    this.showActionForm = true;
    // En producción, cargar lista de técnicos disponibles
  }

  registrarDiagnostico(): void {
    this.currentAction = 'diagnostico';
    this.showActionForm = true;
  }

  aprobarPresupuesto(): void {
    if (!confirm('¿Confirmar aprobación del presupuesto?')) return;

    this.ticketService.aprobarPresupuesto(this.ticketId).subscribe({
      next: () => {
        this.successMessage = 'Presupuesto aprobado exitosamente';
        this.loadTicket();
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al aprobar presupuesto';
      }
    });
  }

  rechazarPresupuesto(): void {
    this.currentAction = 'rechazarPresupuesto';
    this.showActionForm = true;
  }

  iniciarReparacion(): void {
    this.currentAction = 'iniciarReparacion';
    this.showActionForm = true;
  }

  registrarPruebas(): void {
    this.currentAction = 'registrarPruebas';
    this.showActionForm = true;
  }

  marcarListoEntrega(): void {
    if (!confirm('¿Marcar como listo para entrega?')) return;

    this.ticketService.marcarListoEntrega(this.ticketId).subscribe({
      next: () => {
        this.successMessage = 'Ticket marcado como listo para entrega';
        this.loadTicket();
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al marcar como listo';
      }
    });
  }

  registrarEntrega(): void {
    this.currentAction = 'registrarEntrega';
    this.showActionForm = true;
  }

  cancelarTicket(): void {
    this.currentAction = 'cancelar';
    this.showActionForm = true;
  }

  // Form submission
  submitAction(): void {
    const formValue = this.actionForm.value;

    switch (this.currentAction) {
      case 'asignarTecnico':
        if (!formValue.tecnicoId) {
          alert('Selecciona un técnico');
          return;
        }
        this.ticketService.asignarTecnico(this.ticketId, { tecnicoId: formValue.tecnicoId }).subscribe({
          next: () => {
            this.successMessage = 'Técnico asignado exitosamente';
            this.closeActionForm();
            this.loadTicket();
          },
          error: (error) => this.errorMessage = error.message
        });
        break;

      case 'diagnostico':
        if (!formValue.diagnostico || !formValue.costoEstimado) {
          alert('Completa todos los campos requeridos');
          return;
        }
        this.ticketService.registrarDiagnostico(this.ticketId, {
          diagnostico: formValue.diagnostico,
          costoEstimado: formValue.costoEstimado,
          tiempoEstimado: formValue.tiempoEstimado
        }).subscribe({
          next: () => {
            this.successMessage = 'Diagnóstico registrado exitosamente';
            this.closeActionForm();
            this.loadTicket();
          },
          error: (error) => this.errorMessage = error.message
        });
        break;

      case 'rechazarPresupuesto':
        if (!formValue.motivoRechazo) {
          alert('Ingresa el motivo del rechazo');
          return;
        }
        this.ticketService.rechazarPresupuesto(this.ticketId, { motivoRechazo: formValue.motivoRechazo }).subscribe({
          next: () => {
            this.successMessage = 'Presupuesto rechazado';
            this.closeActionForm();
            this.loadTicket();
          },
          error: (error) => this.errorMessage = error.message
        });
        break;

      case 'iniciarReparacion':
        this.ticketService.iniciarReparacion(this.ticketId, {}).subscribe({
          next: () => {
            this.successMessage = 'Reparación iniciada';
            this.closeActionForm();
            this.loadTicket();
          },
          error: (error) => this.errorMessage = error.message
        });
        break;

      case 'registrarPruebas':
        if (!formValue.resultadoPruebas) {
          alert('Ingresa el resultado de las pruebas');
          return;
        }
        this.ticketService.registrarPruebas(this.ticketId, { resultadoPruebas: formValue.resultadoPruebas }).subscribe({
          next: () => {
            this.successMessage = 'Pruebas registradas exitosamente';
            this.closeActionForm();
            this.loadTicket();
          },
          error: (error) => this.errorMessage = error.message
        });
        break;

      case 'registrarEntrega':
        if (!formValue.nombreQuienRecibe) {
          alert('Completa la información de quien recibe');
          return;
        }
        this.ticketService.registrarEntrega(this.ticketId, {
          nombreQuienRecibe: formValue.nombreQuienRecibe,
          parentescoRecibe: formValue.parentescoRecibe || null
        }).subscribe({
          next: () => {
            this.successMessage = 'Entrega registrada exitosamente';
            this.closeActionForm();
            this.loadTicket();
          },
          error: (error) => this.errorMessage = error.message
        });
        break;

      case 'cancelar':
        if (!formValue.motivoCancelacion) {
          alert('Ingresa el motivo de cancelación');
          return;
        }
        this.ticketService.cancelar(this.ticketId, { motivoCancelacion: formValue.motivoCancelacion }).subscribe({
          next: () => {
            this.successMessage = 'Ticket cancelado';
            this.closeActionForm();
            this.loadTicket();
          },
          error: (error) => this.errorMessage = error.message
        });
        break;
    }
  }

  closeActionForm(): void {
    this.showActionForm = false;
    this.currentAction = '';
    this.actionForm.reset();
  }

  // PDF download
  descargarPDF(): void {
    this.isDownloadingPDF = true;
    this.ticketService.descargarPDF(this.ticketId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `Ticket-${this.ticket?.numeroTicket}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
        this.isDownloadingPDF = false;
      },
      error: (error) => {
        this.errorMessage = 'Error al descargar PDF';
        this.isDownloadingPDF = false;
      }
    });
  }

  // Helpers
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
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
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

  // Permission helpers
  canAsignarTecnico(): boolean {
    return this.ticket?.estado === EstadoTicket.INGRESADO &&
           this.authService.hasAnyRole([Rol.ADMINISTRADOR, Rol.RECEPCIONISTA]);
  }

  canRegistrarDiagnostico(): boolean {
    return this.ticket?.estado === EstadoTicket.ASIGNADO &&
           this.authService.hasRole(Rol.TECNICO);
  }

  canAprobarRechazar(): boolean {
    return this.ticket?.estado === EstadoTicket.PRESUPUESTADO &&
           this.authService.hasAnyRole([Rol.ADMINISTRADOR, Rol.RECEPCIONISTA]);
  }

  canIniciarReparacion(): boolean {
    return this.ticket?.estado === EstadoTicket.APROBADO &&
           this.authService.hasRole(Rol.TECNICO);
  }

  canRegistrarPruebas(): boolean {
    return this.ticket?.estado === EstadoTicket.EN_REPARACION &&
           this.authService.hasRole(Rol.TECNICO);
  }

  canMarcarListo(): boolean {
    return this.ticket?.estado === EstadoTicket.EN_PRUEBAS &&
           this.authService.hasRole(Rol.TECNICO);
  }

  canRegistrarEntrega(): boolean {
    return this.ticket?.estado === EstadoTicket.LISTO_ENTREGA &&
           this.authService.hasAnyRole([Rol.ADMINISTRADOR, Rol.RECEPCIONISTA]);
  }

  canCancelar(): boolean {
    return this.ticket?.estado !== EstadoTicket.ENTREGADO &&
           this.ticket?.estado !== EstadoTicket.CANCELADO &&
           this.authService.hasRole(Rol.ADMINISTRADOR);
  }
}
