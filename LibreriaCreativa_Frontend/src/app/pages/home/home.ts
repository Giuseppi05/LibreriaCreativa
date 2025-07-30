import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Searchbar } from '../../components/globalComponents/searchbar/searchbar';
import { Router } from '@angular/router';
import { LoadingService } from '../../services/loading-service';
import { ProductService } from '../../services/product-service';
import { finalize } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Product } from '../../models/product';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [RouterLink, Searchbar, CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  destacados: Product[] = [];

  constructor(
    private router: Router,
    private loading: LoadingService,
    private productService: ProductService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.mostrarDestacados()
  }

  onSearch(query: string) {
    this.router.navigate(['/catalog'], { queryParams: { q: query } });
  }

  mostrarDestacados(): void {
    this.loading.show();

    this.productService
      .obtenerDestacados()
      .pipe(finalize(() => this.loading.hide()))
      .subscribe({
        next: (res) => {
          this.destacados = res;
        },
        error: (err) => {
          const msg =
            err?.error?.message ||
            err?.error?.error ||
            'Error al cargar productos destacados';
          this.toastr.error(msg, 'Ups...');
        },
      });
  }
}
