import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { ClienteService, AuthService } from '../../../core/services';
import { Cliente, PaginatedResponse } from '../../../shared/models';

@Component({
  selector: 'app-lista-clientes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './lista-clientes.html',
  styleUrl: './lista-clientes.scss'
})
export class ListaClientesComponent implements OnInit {
  private clienteService = inject(ClienteService);
  private authService = inject(AuthService);
  private router = inject(Router);

  clientes: Cliente[] = [];
  searchControl = new FormControl('');
  isLoading = false;
  errorMessage = '';

  // Paginación
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  ngOnInit(): void {
    this.loadClientes();

    // Búsqueda con debounce
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(searchTerm => {
        if (searchTerm && searchTerm.trim()) {
          this.buscarClientes(searchTerm.trim());
        } else {
          this.loadClientes();
        }
      });
  }

  loadClientes(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.clienteService.getAll({ page: this.currentPage, size: this.pageSize, sort: 'nombre,asc' })
      .subscribe({
        next: (response) => {
          this.clientes = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error al cargar clientes';
          this.isLoading = false;
        }
      });
  }

  buscarClientes(nombre: string): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.clienteService.buscarPorNombre(nombre, { page: 0, size: this.pageSize })
      .subscribe({
        next: (response) => {
          this.clientes = response.data.content;
          this.totalElements = response.data.totalElements;
          this.totalPages = response.data.totalPages;
          this.currentPage = 0;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error en la búsqueda';
          this.isLoading = false;
        }
      });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadClientes();
  }

  eliminarCliente(id: number): void {
    if (!confirm('¿Estás seguro de eliminar este cliente?')) {
      return;
    }

    this.clienteService.delete(id).subscribe({
      next: () => {
        this.loadClientes();
      },
      error: (error) => {
        alert('Error al eliminar cliente: ' + (error.message || 'Error desconocido'));
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
