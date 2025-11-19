import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { TicketService, ClienteService } from '../../../core/services';
import { Cliente, CrearTicketDTO } from '../../../shared/models';

@Component({
  selector: 'app-form-ticket',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './form-ticket.html',
  styleUrl: './form-ticket.scss'
})
export class FormTicketComponent implements OnInit {
  private fb = inject(FormBuilder);
  private ticketService = inject(TicketService);
  private clienteService = inject(ClienteService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  ticketForm: FormGroup;
  isSaving = false;
  errorMessage = '';
  successMessage = '';

  // Cliente selection
  clientes: Cliente[] = [];
  isLoadingClientes = false;
  clienteSearchTerm = '';

  // Prioridades disponibles
  prioridades = ['BAJA', 'MEDIA', 'ALTA', 'URGENTE'];

  constructor() {
    this.ticketForm = this.fb.group({
      clienteId: [null, [Validators.required]],
      tipoEquipo: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      marca: ['', [Validators.maxLength(50)]],
      modelo: ['', [Validators.maxLength(50)]],
      numeroSerie: ['', [Validators.maxLength(100)]],
      accesorios: ['', [Validators.maxLength(500)]],
      problemaReportado: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
      observaciones: ['', [Validators.maxLength(1000)]],
      prioridad: ['MEDIA', [Validators.required]]
    });
  }

  ngOnInit(): void {
    // Cargar clientes iniciales
    this.loadClientes();

    // Si viene clienteId por query params, preseleccionarlo
    const clienteId = this.route.snapshot.queryParams['clienteId'];
    if (clienteId) {
      this.ticketForm.patchValue({ clienteId: +clienteId });
    }
  }

  loadClientes(searchTerm: string = ''): void {
    this.isLoadingClientes = true;

    if (searchTerm.trim()) {
      this.clienteService.buscar(searchTerm, { page: 0, size: 20 }).subscribe({
        next: (response) => {
          this.clientes = response.data.content;
          this.isLoadingClientes = false;
        },
        error: (error) => {
          console.error('Error al cargar clientes:', error);
          this.isLoadingClientes = false;
        }
      });
    } else {
      this.clienteService.getAll({ page: 0, size: 20 }).subscribe({
        next: (response) => {
          this.clientes = response.data.content;
          this.isLoadingClientes = false;
        },
        error: (error) => {
          console.error('Error al cargar clientes:', error);
          this.isLoadingClientes = false;
        }
      });
    }
  }

  onClienteSearchChange(event: Event): void {
    const searchTerm = (event.target as HTMLInputElement).value;
    this.clienteSearchTerm = searchTerm;

    // Simple debounce manual
    setTimeout(() => {
      if (this.clienteSearchTerm === searchTerm) {
        this.loadClientes(searchTerm);
      }
    }, 300);
  }

  onSubmit(): void {
    if (this.ticketForm.invalid) {
      this.markFormGroupTouched(this.ticketForm);
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.ticketForm.value;

    const ticketData: CrearTicketDTO = {
      clienteId: formValue.clienteId,
      tipoEquipo: formValue.tipoEquipo.trim(),
      marca: formValue.marca?.trim() || null,
      modelo: formValue.modelo?.trim() || null,
      numeroSerie: formValue.numeroSerie?.trim() || null,
      accesorios: formValue.accesorios?.trim() || null,
      problemaReportado: formValue.problemaReportado.trim(),
      observaciones: formValue.observaciones?.trim() || null,
      prioridad: formValue.prioridad
    };

    this.ticketService.create(ticketData).subscribe({
      next: (response) => {
        this.successMessage = `Ticket ${response.data.numeroTicket} creado exitosamente`;
        setTimeout(() => {
          this.router.navigate(['/tickets', response.data.id]);
        }, 1500);
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al crear el ticket';
        this.isSaving = false;
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/tickets']);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  // Getters para validación
  get clienteId() { return this.ticketForm.get('clienteId'); }
  get tipoEquipo() { return this.ticketForm.get('tipoEquipo'); }
  get marca() { return this.ticketForm.get('marca'); }
  get modelo() { return this.ticketForm.get('modelo'); }
  get numeroSerie() { return this.ticketForm.get('numeroSerie'); }
  get accesorios() { return this.ticketForm.get('accesorios'); }
  get problemaReportado() { return this.ticketForm.get('problemaReportado'); }
  get observaciones() { return this.ticketForm.get('observaciones'); }
  get prioridad() { return this.ticketForm.get('prioridad'); }

  getErrorMessage(controlName: string): string {
    const control = this.ticketForm.get(controlName);
    if (!control || !control.touched || !control.errors) {
      return '';
    }

    const errors = control.errors;
    if (errors['required']) return 'Este campo es requerido';
    if (errors['minlength']) return `Mínimo ${errors['minlength'].requiredLength} caracteres`;
    if (errors['maxlength']) return `Máximo ${errors['maxlength'].requiredLength} caracteres`;
    return 'Campo inválido';
  }

  getSelectedClienteName(): string {
    const clienteId = this.ticketForm.get('clienteId')?.value;
    if (!clienteId) return '';
    const cliente = this.clientes.find(c => c.id === clienteId);
    return cliente ? cliente.nombre : '';
  }
}
