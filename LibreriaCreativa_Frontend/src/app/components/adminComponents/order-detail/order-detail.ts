import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Order } from '../../../models/order';
import { ModalComponent } from '../../globalComponents/modal-component/modal-component';
import { ModalVoucherService } from '../../../services/modal-voucher-service';

@Component({
  selector: 'app-order-detail',
  imports: [CommonModule, ModalComponent],
  templateUrl: './order-detail.html',
  styleUrl: './order-detail.css'
})
export class OrderDetail {
  @Input() order: Order | null = null;

  selectedYapeImage: string | null = null;

  constructor(public modalService: ModalVoucherService) {}

  openYapeModal(imageUrl: string | undefined) {
    if (imageUrl) {
      this.selectedYapeImage = imageUrl;
      this.modalService.open();
    }
  }

  closeYapeModal() {
    this.selectedYapeImage = null;
    this.modalService.close();
  }
}
