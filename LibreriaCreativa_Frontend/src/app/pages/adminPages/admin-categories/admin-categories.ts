import { Component } from '@angular/core';
import { ModalComponent } from '../../../components/globalComponents/modal-component/modal-component';
import { FormCategories } from '../../../components/adminComponents/form-categories/form-categories';
import { CommonModule } from '@angular/common';
import { Category } from '../../../models/category';
import { CategoryService } from '../../../services/category-service';
import { LoadingService } from '../../../services/loading-service';
import { ModalService } from '../../../services/modal-service';
import { finalize } from 'rxjs';
import { PageCategories } from '../../../models/PageModels/page-categories';

@Component({
  selector: 'app-admin-categories',
  imports: [ModalComponent, FormCategories, CommonModule],
  templateUrl: './admin-categories.html',
  styleUrl: './admin-categories.css',
})
export class AdminCategories {
  page?: PageCategories;
  size = 10;
  error?: string;
  success?: string;
  categoryToEdit?: Category;

  constructor(
    private categoryService: CategoryService,
    private loadingService: LoadingService,
    public modalService: ModalService
  ) {}

  ngOnInit() {
    this.cargar(0);
  }

  cargar(page: number): void {
    this.loadingService.show();

    this.categoryService
      .getCategoriesAdmin(page, this.size)
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: (data) => {
          this.page = data;
          this.error = undefined;
        },
        error: () => {
          this.error = 'Error al cargar las categorias.';
        },
      });
  }

  borrar(id: number): void {
    if (confirm('¿Seguro que deseas eliminar esta categoría?')) {
      this.loadingService.show();

      this.categoryService
        .deleteCategory(id)
        .pipe(finalize(() => this.loadingService.hide()))
        .subscribe({
          next: () => {
            this.success = 'Categoría eliminada correctamente.';
            this.cargar(this.page?.number || 0);
          },
          error: () => {
            this.error = 'No se pudo eliminar el categoría.';
          },
        });
    }
  }

  totalPagesArray(): number[] {
    return this.page
      ? Array.from({ length: this.page.totalPages }, (_, i) => i)
      : [];
  }

  openModal(category?: Category) {
    this.categoryToEdit = category;
    this.modalService.open();
  }

  closeModal() {
    this.modalService.close();
  }

  onCategorySaved(action: 'add' | 'edit'): void {
    this.closeModal();
    this.success =
      action === 'add'
        ? 'Categoría añadida correctamente'
        : 'Categoría editada correctamente';
    this.cargar(this.page?.number || 0);
  }

  editar(category: Category): void {
    this.openModal(category);
  }
}
