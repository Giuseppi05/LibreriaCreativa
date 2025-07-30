import { Component } from '@angular/core';
import { Input } from '@angular/core';
import { Product } from '../../../models/product';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-product-card',
  imports: [RouterLink],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css'
})

export class ProductCardComponent {
  @Input() producto!: Product;
}