import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-modal-component',
  imports: [],
  templateUrl: './modal-component.html',
  styleUrl: './modal-component.css',
})
export class ModalComponent {
  @Input() title = '';
  @Output() closed = new EventEmitter<void>();

  onClose() {
    this.closed.emit();
  }
}
