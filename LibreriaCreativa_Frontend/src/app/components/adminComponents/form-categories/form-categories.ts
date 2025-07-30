import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Category } from '../../../models/category';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoadingService } from '../../../services/loading-service';
import { CategoryService } from '../../../services/category-service';
import { finalize } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-form-categories',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './form-categories.html',
  styleUrl: './form-categories.css',
})
export class FormCategories {
  @Input() initialCategory?: Category;
  @Output() saved = new EventEmitter<'add' | 'edit'>();

  form: FormGroup;
  error?: string;
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private loadingService: LoadingService,
    private categoryService: CategoryService
  ) {
    this.form = this.fb.group({
      id: [],
      name: ['', Validators.required],
    });
  }

  ngOnInit() {
    if (this.initialCategory) {
      this.isEditMode = true;
      this.patchForm(this.initialCategory);
    }
  }

  patchForm(c: Category): void {
    this.form.patchValue({
      id: c.id,
      name: c.name,
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.error = 'Completa todos los campos obligatorios.';
      this.form.markAllAsTouched();
      return;
    }

    this.error = undefined;
    this.loadingService.show();

    const categoryData: Category = this.form.value;

    const request$ = this.isEditMode
      ? this.categoryService.updateCategory(categoryData)
      : this.categoryService.saveCategory(categoryData);

    request$
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: () => this.saved.emit(this.isEditMode ? 'edit' : 'add'),
        error: (err) => (this.error = err.error.error),
      });
  }

  controlInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
