import { Component, EventEmitter, Output, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoryService } from '../../../services/category-service';
import { Category } from '../../../models/category';
import { CatalogFilters } from '../../../models/PageModels/catalog-filters';

@Component({
  selector: 'app-catalog-filter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './catalog-filter.html',
  styleUrl: './catalog-filter.css',
})
export class CatalogFilter {
  @Input() selectedFilters: CatalogFilters = { categorias: [] };
  @Output() filtersChange = new EventEmitter<CatalogFilters>();

  categories: Category[] = [];

  selectedCategorias: string[] = [];
  selectedPrecio?: string;
  selectedStock?: string;

  precios = [
    { value: '0-10', label: 'S/. 0 - S/. 10' },
    { value: '11-50', label: 'S/. 11 - S/. 50' },
    { value: '51-100', label: 'S/. 51 - S/. 100' },
    { value: '+101', label: 'Más de S/. 100' },
  ];

  constructor(private categoryService: CategoryService) {}

  ngOnInit() {
    this.categoryService.obtenerCategorias().subscribe({
      next: (data: Category[]) => {
        this.categories = data;
      },
      error: (error) => {
        console.error('Error al obtener categorías:', error);
      },
    });

    // Inicializa desde Input
    if (this.selectedFilters) {
      this.selectedCategorias = [...(this.selectedFilters.categorias || [])];
      this.selectedPrecio = this.selectedFilters.precio;
      this.selectedStock = this.selectedFilters.stock;
    }
  }

  toggleCategoria(cat: string) {
    if (this.selectedCategorias.includes(cat)) {
      this.selectedCategorias = this.selectedCategorias.filter(c => c !== cat);
    } else {
      this.selectedCategorias.push(cat);
    }
  }

  aplicarFiltros() {
    this.filtersChange.emit({
      categorias: this.selectedCategorias,
      precio: this.selectedPrecio,
      stock: this.selectedStock
    });
  }

  limpiarFiltros() {
    this.selectedCategorias = [];
    this.selectedPrecio = undefined;
    this.selectedStock = undefined;
    this.aplicarFiltros();
  }
}