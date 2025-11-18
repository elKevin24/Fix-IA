import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { PiezaService, AuthService } from '../../../core/services';
import { Pieza, Rol } from '../../../shared/models';

@Component({
  selector: 'app-lista-piezas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './lista-piezas.html',
  styleUrl: './lista-piezas.scss'
})
export class ListaPiezasComponent implements OnInit {
  private piezaService = inject(PiezaService);
  private authService = inject(AuthService);
  private router = inject(Router);

  piezas: Pieza[] = [];
  searchControl = new FormControl('');
  categoriaFilterControl = new FormControl('');
  stockFilterControl = new FormControl('');
  isLoading = false;
  errorMessage = '';

  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  // Categorías (se cargarían del backend)
  categorias: string[] = [];

  // Filtros de stock
  stockFilters = [
    { value: '', label: 'Todos' },
    { value: 'SIN_STOCK', label: 'Sin Stock' },
    { value: 'STOCK_BAJO', label: 'Stock Bajo' },
    { value: 'STOCK_NORMAL', label: 'Stock Normal' }
  ];

  ngOnInit(): void {
    this.loadPiezas();
    this.loadCategorias();

    // Search con debounce
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(searchTerm => {
        this.currentPage = 0;
        if (searchTerm && searchTerm.trim()) {
          this.buscarPiezas(searchTerm.trim());
        } else {
          this.loadPiezas();
        }
      });

    // Filtro por categoría
    this.categoriaFilterControl.valueChanges.subscribe(() => {
      this.currentPage = 0;
      this.loadPiezas();
    });

    // Filtro por stock
    this.stockFilterControl.valueChanges.subscribe(() => {
      this.currentPage = 0;
      this.loadPiezas();
    });
  }

  loadPiezas(): void {
    this.isLoading = true;
    this.errorMessage = '';

    const categoria = this.categoriaFilterControl.value;
    const estadoStock = this.stockFilterControl.value;

    // Aplicar filtros
    if (categoria) {
      this.piezaService.buscarPorCategoria(categoria, { page: this.currentPage, size: this.pageSize })
        .subscribe({
          next: (response) => {
            this.piezas = response.data.content;
            this.totalElements = response.data.totalElements;
            this.totalPages = response.data.totalPages;
            this.isLoading = false;
          },
          error: (error) => {
            this.errorMessage = error.message || 'Error al cargar piezas';
            this.isLoading = false;
          }
        });
    } else if (estadoStock === 'STOCK_BAJO') {
      this.piezaService.getPiezasConStockBajo({ page: this.currentPage, size: this.pageSize })
        .subscribe({
          next: (response) => {
            this.piezas = response.data.content;
            this.totalElements = response.data.totalElements;
            this.totalPages = response.data.totalPages;
            this.isLoading = false;
          },
          error: (error) => {
            this.errorMessage = error.message || 'Error al cargar piezas';
            this.isLoading = false;
          }
        });
    } else {
      this.piezaService.getAll({ page: this.currentPage, size: this.pageSize })
        .subscribe({
          next: (response) => {
            this.piezas = response.data.content;
            this.totalElements = response.data.totalElements;
            this.totalPages = response.data.totalPages;
            this.isLoading = false;
          },
          error: (error) => {
            this.errorMessage = error.message || 'Error al cargar piezas';
            this.isLoading = false;
          }
        });
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

  buscarPiezas(searchTerm: string): void {
    this.isLoading = true;
    this.piezaService.buscarGlobal(searchTerm, { page: this.currentPage, size: this.pageSize })
      .subscribe({
        next: (response) => {
          this.piezas = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error al buscar piezas';
          this.isLoading = false;
        }
      });
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadPiezas();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  clearFilters(): void {
    this.searchControl.setValue('');
    this.categoriaFilterControl.setValue('');
    this.stockFilterControl.setValue('');
    this.currentPage = 0;
    this.loadPiezas();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getEstadoStockBadgeClass(estadoStock: string): string {
    const classes: { [key: string]: string } = {
      'SIN_STOCK': 'bg-danger',
      'STOCK_BAJO': 'bg-warning text-dark',
      'STOCK_NORMAL': 'bg-success'
    };
    return classes[estadoStock] || 'bg-secondary';
  }

  formatEstadoStock(estado: string): string {
    return estado.replace(/_/g, ' ');
  }

  canManageInventory(): boolean {
    return this.authService.hasAnyRole([Rol.ADMINISTRADOR, Rol.TECNICO]);
  }
}
