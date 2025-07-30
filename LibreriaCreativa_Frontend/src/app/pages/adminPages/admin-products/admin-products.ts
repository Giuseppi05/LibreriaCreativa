import { Component } from '@angular/core';
import { ProductPage } from '../../../models/PageModels/page-products';
import { ProductService } from '../../../services/product-service';
import { CommonModule } from '@angular/common';
import { Product } from '../../../models/product';
import { LoadingService } from '../../../services/loading-service';
import { finalize } from 'rxjs';
import { ModalService } from '../../../services/modal-service';
import { ModalComponent } from '../../../components/globalComponents/modal-component/modal-component';
import { FormProducts } from '../../../components/adminComponents/form-products/form-products';

@Component({
  selector: 'app-admin-products',
  imports: [CommonModule, ModalComponent, FormProducts],
  templateUrl: './admin-products.html',
  styleUrl: './admin-products.css',
})
export class AdminProducts {
  page?: ProductPage;
  size = 5;
  error?: string;
  success?: string;
  productToEdit?: Product;

  constructor(
    private productService: ProductService,
    private loadingService: LoadingService,
    public modalService: ModalService
  ) {}

  ngOnInit(): void {
    this.cargar(0);
  }

  cargar(page: number): void {
    this.loadingService.show();

    this.productService
      .obtenerProductosAdmin(page, this.size)
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: (data) => {
          this.page = data;
          this.error = undefined;
        },
        error: () => {
          this.error = 'Error al cargar productos.';
        },
      });
  }

  borrar(id: number): void {
    if (confirm('¿Seguro que deseas eliminar este producto?')) {
      this.loadingService.show();

      this.productService
        .eliminarProducto(id)
        .pipe(finalize(() => this.loadingService.hide()))
        .subscribe({
          next: () => {
            this.success = 'Producto eliminado correctamente.';
            this.cargar(this.page?.number || 0);
          },
          error: () => {
            this.error = 'No se pudo eliminar el producto.';
          },
        });
    }
  }

  totalPagesArray(): number[] {
    return this.page
      ? Array.from({ length: this.page.totalPages }, (_, i) => i)
      : [];
  }

  openModal(product?: Product) {
    this.productToEdit = product;
    this.modalService.open();
  }

  closeModal() {
    this.modalService.close();
  }

  onProductSaved(action: 'add' | 'edit'): void {
    this.closeModal();
    this.success =
      action === 'add'
        ? 'Producto añadido correctamente'
        : 'Producto editado correctamente';
    this.cargar(this.page?.number || 0);
  }

  editar(producto: Product): void {
    this.openModal(producto);
  }
}
