import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-error',
  imports: [CommonModule, RouterLink],
  templateUrl: './error.html',
  styleUrl: './error.css',
})
export class Error {
  errorCode: number = 404;

  constructor(private route: ActivatedRoute) {
    this.route.paramMap.subscribe((params) => {
      const code = params.get('code');
      this.errorCode = code ? +code : 404;
    });
  }
}
