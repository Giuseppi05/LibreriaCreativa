import { Component } from '@angular/core';
import { Input, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-searchbar',
  imports: [CommonModule, FormsModule],
  templateUrl: './searchbar.html',
  styleUrl: './searchbar.css'
})
export class Searchbar {
  @Input() query = '';
  @Output() search = new EventEmitter<string>();

  onSubmit() {
    this.search.emit(this.query);
  }
}
