import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ClienteService } from '../../../core/services';
import { CrearClienteDTO, ActualizarClienteDTO, Cliente } from '../../../shared/models';

@Component({
  selector: 'app-form-cliente',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './form-cliente.html',
  styleUrl: './form-cliente.scss'
})
export class FormClienteComponent implements OnInit {
  private fb = inject(FormBuilder);
  private clienteService = inject(ClienteService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  clienteForm: FormGroup;
  isEditMode = false;
  clienteId: number | null = null;
  isLoading = false;
  isSaving = false;
  errorMessage = '';
  successMessage = '';

  constructor() {
    this.clienteForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      telefono: ['', [Validators.required, Validators.pattern(/^\d{4}-\d{4}$/)]],
      direccion: ['', [Validators.maxLength(200)]],
      dui: ['', [Validators.pattern(/^\d{8}-\d$/)]],
      nit: ['', [Validators.pattern(/^\d{4}-\d{6}-\d{3}-\d$/)]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.clienteId = +id;
      this.loadCliente();
    }
  }

  loadCliente(): void {
    if (!this.clienteId) return;

    this.isLoading = true;
    this.errorMessage = '';

    this.clienteService.getById(this.clienteId).subscribe({
      next: (response) => {
        const cliente = response.data;
        this.clienteForm.patchValue({
          nombre: cliente.nombre,
          email: cliente.email,
          telefono: cliente.telefono,
          direccion: cliente.direccion || '',
          dui: cliente.dui || '',
          nit: cliente.nit || ''
        });
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al cargar el cliente';
        this.isLoading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.clienteForm.invalid) {
      this.markFormGroupTouched(this.clienteForm);
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.clienteForm.value;

    // Limpiar campos opcionales vacíos
    const clienteData = {
      nombre: formValue.nombre.trim(),
      email: formValue.email.trim(),
      telefono: formValue.telefono.trim(),
      direccion: formValue.direccion?.trim() || null,
      dui: formValue.dui?.trim() || null,
      nit: formValue.nit?.trim() || null
    };

    if (this.isEditMode && this.clienteId) {
      // Actualizar cliente existente
      this.clienteService.update(this.clienteId, clienteData as ActualizarClienteDTO).subscribe({
        next: () => {
          this.successMessage = 'Cliente actualizado exitosamente';
          setTimeout(() => {
            this.router.navigate(['/clientes']);
          }, 1500);
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error al actualizar el cliente';
          this.isSaving = false;
        }
      });
    } else {
      // Crear nuevo cliente
      this.clienteService.create(clienteData as CrearClienteDTO).subscribe({
        next: () => {
          this.successMessage = 'Cliente creado exitosamente';
          setTimeout(() => {
            this.router.navigate(['/clientes']);
          }, 1500);
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error al crear el cliente';
          this.isSaving = false;
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/clientes']);
  }

  // Helper para marcar todos los campos como tocados (mostrar errores de validación)
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  // Getters para validación en el template
  get nombre() { return this.clienteForm.get('nombre'); }
  get email() { return this.clienteForm.get('email'); }
  get telefono() { return this.clienteForm.get('telefono'); }
  get direccion() { return this.clienteForm.get('direccion'); }
  get dui() { return this.clienteForm.get('dui'); }
  get nit() { return this.clienteForm.get('nit'); }

  // Helper para mostrar mensajes de error específicos
  getErrorMessage(controlName: string): string {
    const control = this.clienteForm.get(controlName);
    if (!control || !control.touched || !control.errors) {
      return '';
    }

    const errors = control.errors;
    if (errors['required']) return 'Este campo es requerido';
    if (errors['email']) return 'Ingrese un email válido';
    if (errors['minlength']) return `Mínimo ${errors['minlength'].requiredLength} caracteres`;
    if (errors['maxlength']) return `Máximo ${errors['maxlength'].requiredLength} caracteres`;
    if (errors['pattern']) {
      if (controlName === 'telefono') return 'Formato: 0000-0000';
      if (controlName === 'dui') return 'Formato: 00000000-0';
      if (controlName === 'nit') return 'Formato: 0000-000000-000-0';
    }
    return 'Campo inválido';
  }
}
