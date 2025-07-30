import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { Order } from '../../../models/order';
import { OrderStatus } from '../../../models/order-status';
import { OrderStatusService } from '../../../services/order-status-service';
import { LoadingService } from '../../../services/loading-service';

@Component({
  selector: 'app-status-change',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './status-change.html',
  styleUrl: './status-change.css',
})
export class StatusChange {
  @Input() order: Order | null = null;
  @Output() statusChanged = new EventEmitter<{ orderId: number, nuevoEstadoId: number }>();

  states: OrderStatus[] = [];
  statusForm!: FormGroup;
  error? = '';
  loading = false;

  constructor(
    private fb: FormBuilder,
    private orderStatusService: OrderStatusService,
    private loadingService: LoadingService
  ) {}

  ngOnInit() {
    this.initForm();
    this.chargeStatus();
  }

  initForm() {
    this.statusForm = this.fb.group({
      estadoId: ['', Validators.required],
    });
  }

  chargeStatus() {
    this.loading = true;
    this.loadingService.show();

    this.orderStatusService.obtenerCategorias()
      .pipe(finalize(() => {
        this.loading = false;
        this.loadingService.hide();
      }))
      .subscribe({
        next: (data) => {
          this.states = data;
          this.error = undefined;
        },
        error: () => {
          this.error = 'Error al cargar los estados.';
        }
      });
  }

  submit() {
    if (this.statusForm.invalid || !this.order) return;

    const nuevoEstadoId = this.statusForm.value.estadoId;
    this.statusChanged.emit({
      orderId: this.order.id,
      nuevoEstadoId: +nuevoEstadoId
    });
  }
}