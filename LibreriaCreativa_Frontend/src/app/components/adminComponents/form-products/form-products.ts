import { Component, EventEmitter, Output, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { ProductService } from '../../../services/product-service';
import { finalize } from 'rxjs';
import { LoadingService } from '../../../services/loading-service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Category } from '../../../models/category';
import { CategoryService } from '../../../services/category-service';
import { Product } from '../../../models/product';

@Component({
  selector: 'app-form-products',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './form-products.html',
  styleUrl: './form-products.css',
})
export class FormProducts {
  @Input() initialProduct?: Product;
  @Output() saved = new EventEmitter<'add' | 'edit'>();

  form: FormGroup;
  imagePreview: string | null = null;
  error?: string;
  categories: Category[] = [];
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private loadingService: LoadingService,
    private categoryService: CategoryService
  ) {
    this.form = this.fb.group({
      id: [],
      name: ['', Validators.required],
      marca: ['', Validators.required],
      precio: [0, [Validators.required, Validators.min(0)]],
      stock: [0, [Validators.required, Validators.min(0)]],
      categoryId: [null, Validators.required],
      description: ['', Validators.required],
      specs: this.fb.array([]),
      active: [true],
      imageFile: [null], 
      img: [''],
    });
  }

  ngOnInit() {
    this.loadingService.show();
    this.categoryService
      .obtenerCategorias()
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: (data) => {
          this.categories = data;
          this.error = undefined;
          if (this.initialProduct) {
            this.isEditMode = true;
            this.patchForm(this.initialProduct);
          }
        },
        error: () => {
          this.error = 'Error al cargar categorías.';
        },
      });
  }

  patchForm(product: Product): void {
    if (!product.specsMap && typeof product.specs === 'string') {
      try {
        product.specsMap = JSON.parse(product.specs);
      } catch {
        product.specsMap = {};
      }
    }

    this.form.patchValue({
      id: product.id,
      name: product.name,
      marca: product.marca,
      precio: product.precio,
      stock: product.stock,
      categoryId: product.category?.id,
      description: product.description,
      active: product.active,
      imageFile: null,
      img: product.img || '',
    });

    this.specs.clear();
    if (product.specsMap && typeof product.specsMap === 'object') {
      for (const [name, value] of Object.entries(product.specsMap)) {
        this.specs.push(
          this.fb.group({
            name: [name, Validators.required],
            value: [value, Validators.required],
          })
        );
      }
    }

    this.imagePreview = product.img || null;
  }

  get specs(): FormArray {
    return this.form.get('specs') as FormArray;
  }

  addSpec(): void {
    this.specs.push(
      this.fb.group({
        name: ['', Validators.required],
        value: ['', Validators.required],
      })
    );
  }

  removeSpec(index: number): void {
    this.specs.removeAt(index);
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      const file = input.files[0];
      this.form.patchValue({ imageFile: file });

      const reader = new FileReader();
      reader.onload = () => (this.imagePreview = reader.result as string);
      reader.readAsDataURL(file);
    }
  }

  submit(): void {
    // Validar que la imagen sea obligatoria solo al crear
    if (!this.isEditMode && !this.form.get('imageFile')?.value) {
      this.error = 'La imagen es obligatoria para productos nuevos.';
      return;
    }

    if (this.form.invalid) {
      this.error = 'Completa todos los campos obligatorios.';
      this.form.markAllAsTouched();
      return;
    }

    this.error = undefined;
    this.loadingService.show();

    const formData = new FormData();
    for (const [key, value] of Object.entries(this.form.value)) {
      if (key === 'specs') {
        const specsJson = JSON.stringify(
          this.specs.value.reduce(
            (
              acc: Record<string, string>,
              curr: { name: string; value: string }
            ) => {
              acc[curr.name] = curr.value;
              return acc;
            },
            {}
          )
        );
        formData.append('specs', specsJson);
      } else if (key === 'imageFile' && value instanceof File) {
        formData.append('imageFile', value);
      } else if (value !== null && value !== undefined) {
        formData.append(key, String(value));
      }
    }

    this.productService
      .guardarProducto(formData)
      .pipe(finalize(() => this.loadingService.hide()))
      .subscribe({
        next: () => this.saved.emit(this.isEditMode ? 'edit' : 'add'),
        error: () => (this.error = 'No se pudo guardar el producto.'),
      });
  }

  controlInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
