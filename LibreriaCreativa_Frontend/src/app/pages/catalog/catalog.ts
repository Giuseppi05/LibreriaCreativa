import { ProductCardComponent } from '../../components/globalComponents/product-card/product-card';
import { CatalogFilter } from '../../components/catalogComponents/catalog-filter/catalog-filter';
import { Searchbar } from '../../components/globalComponents/searchbar/searchbar';
import { ProductPage } from '../../models/PageModels/page-products';
import { ProductService } from '../../services/product-service';
import { LoadingService } from '../../services/loading-service';
import { ActivatedRoute } from '@angular/router';
import { Product } from '../../models/product';
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { finalize } from 'rxjs';
import { CatalogFilters } from '../../models/PageModels/catalog-filters';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CatalogFilter, Searchbar, ProductCardComponent, CommonModule],
  templateUrl: './catalog.html',
  styleUrl: './catalog.css',
})
export class Catalog {
  products: Product[] = [];
  tamañoPagina = 8;
  totalPaginas = 0;
  paginaActual = 0;
  totalElementos = 0;
  elementosEnPagina = 0;
  currentSort = 'default';
  currentSearch = '';
  filters: CatalogFilters = { categorias: [] };

  constructor(
    private productService: ProductService,
    private loadingService: LoadingService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.currentSearch = params['q'] || '';

      // LEER categorias desde la query
      const categoriasParam = params['categorias'];
      if (categoriasParam) {
        if (Array.isArray(categoriasParam)) {
          this.filters.categorias = categoriasParam;
        } else {
          this.filters.categorias = [categoriasParam];
        }
      } else {
        this.filters.categorias = [];
      }

      this.paginaActual = 0;
      this.cargarProductos();
    });
  }

  cargarProductos(page: number = 0) {
    this.loadingService.show();

    this.productService
      .obtenerProductosPaginados(
        page,
        this.tamañoPagina,
        this.currentSort,
        this.currentSearch,
        this.filters
      )
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: (data: ProductPage) => {
          this.products = data.content;
          this.totalPaginas = data.totalPages;
          this.paginaActual = data.number;
          this.totalElementos = data.totalElements;
          this.elementosEnPagina = data.content.length;
        },
        error: (error) => {
          console.error('Error fetching products:', error);
        },
      });
  }

  onSortChange(event: Event) {
    const selectElement = event.target as HTMLSelectElement;
    this.currentSort = selectElement.value;
    this.paginaActual = 0;
    this.cargarProductos();
  }

  onSearch(query: string) {
    this.currentSearch = query;
    this.paginaActual = 0;
    this.cargarProductos();
  }

  onFiltersChange(newFilters: CatalogFilters) {
    this.filters = newFilters;
    this.paginaActual = 0;
    this.cargarProductos();
  }

  get inicio(): number {
    return this.totalElementos === 0
      ? 0
      : this.paginaActual * this.tamañoPagina + 1;
  }

  get fin(): number {
    return this.paginaActual * this.tamañoPagina + this.elementosEnPagina;
  }

  get paginas(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i);
  }
}
