import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PiezaService } from '../../../core/services';
import { CrearPiezaDTO, ActualizarPiezaDTO, Pieza } from '../../../shared/models';

@Component({
  selector: 'app-form-pieza',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './form-pieza.html',
  styleUrl: './form-pieza.scss'
})
export class FormPiezaComponent implements OnInit {
  private fb = inject(FormBuilder);
  private piezaService = inject(PiezaService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  piezaForm: FormGroup;
  isEditMode = false;
  piezaId: number | null = null;
  isLoading = false;
  isSaving = false;
  errorMessage = '';
  successMessage = '';

  categorias: string[] = [];

  constructor() {
    this.piezaForm = this.fb.group({
      codigo: ['', [Validators.required, Validators.maxLength(50)]],
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.maxLength(500)]],
      categoria: ['', [Validators.required, Validators.maxLength(50)]],
      precioCosto: [0, [Validators.required, Validators.min(0)]],
      precioVenta: [0, [Validators.required, Validators.min(0)]],
      stock: [0, [Validators.required, Validators.min(0)]],
      stockMinimo: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.loadCategorias();

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.piezaId = +id;
      this.loadPieza();
    }
  }

  loadCategorias(): void {
    this.piezaService.getCategorias().subscribe({
      next: (response) => {
        this.categorias = response.data;
      },
      error: (error) => {
        console.error('Error al cargar categorías:', error);
      }
    });
  }

  loadPieza(): void {
    if (!this.piezaId) return;

    this.isLoading = true;
    this.piezaService.getById(this.piezaId).subscribe({
      next: (response) => {
        const pieza = response.data;
        this.piezaForm.patchValue({
          codigo: pieza.codigo,
          nombre: pieza.nombre,
          descripcion: pieza.descripcion || '',
          categoria: pieza.categoria,
          precioCosto: pieza.precioCosto,
          precioVenta: pieza.precioVenta,
          stock: pieza.stock,
          stockMinimo: pieza.stockMinimo
        });
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al cargar la pieza';
        this.isLoading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.piezaForm.invalid) {
      this.markFormGroupTouched(this.piezaForm);
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formValue = this.piezaForm.value;
    const piezaData = {
      codigo: formValue.codigo.trim(),
      nombre: formValue.nombre.trim(),
      descripcion: formValue.descripcion?.trim() || null,
      categoria: formValue.categoria.trim(),
      precioCosto: parseFloat(formValue.precioCosto),
      precioVenta: parseFloat(formValue.precioVenta),
      stock: parseInt(formValue.stock),
      stockMinimo: parseInt(formValue.stockMinimo)
    };

    if (this.isEditMode && this.piezaId) {
      this.piezaService.update(this.piezaId, piezaData as ActualizarPiezaDTO).subscribe({
        next: () => {
          this.successMessage = 'Pieza actualizada exitosamente';
          setTimeout(() => {
            this.router.navigate(['/inventario']);
          }, 1500);
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error al actualizar la pieza';
          this.isSaving = false;
        }
      });
    } else {
      this.piezaService.create(piezaData as CrearPiezaDTO).subscribe({
        next: () => {
          this.successMessage = 'Pieza creada exitosamente';
          setTimeout(() => {
            this.router.navigate(['/inventario']);
          }, 1500);
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error al crear la pieza';
          this.isSaving = false;
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/inventario']);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  // Getters
  get codigo() { return this.piezaForm.get('codigo'); }
  get nombre() { return this.piezaForm.get('nombre'); }
  get descripcion() { return this.piezaForm.get('descripcion'); }
  get categoria() { return this.piezaForm.get('categoria'); }
  get precioCosto() { return this.piezaForm.get('precioCosto'); }
  get precioVenta() { return this.piezaForm.get('precioVenta'); }
  get stock() { return this.piezaForm.get('stock'); }
  get stockMinimo() { return this.piezaForm.get('stockMinimo'); }

  getErrorMessage(controlName: string): string {
    const control = this.piezaForm.get(controlName);
    if (!control || !control.touched || !control.errors) {
      return '';
    }

    const errors = control.errors;
    if (errors['required']) return 'Este campo es requerido';
    if (errors['min']) return `Valor mínimo: ${errors['min'].min}`;
    if (errors['maxlength']) return `Máximo ${errors['maxlength'].requiredLength} caracteres`;
    return 'Campo inválido';
  }

  calculateMargin(): string {
    const costo = this.piezaForm.get('precioCosto')?.value;
    const venta = this.piezaForm.get('precioVenta')?.value;
    if (costo > 0 && venta > 0) {
      const margin = ((venta - costo) / costo) * 100;
      return margin.toFixed(1) + '%';
    }
    return '0%';
  }
}
