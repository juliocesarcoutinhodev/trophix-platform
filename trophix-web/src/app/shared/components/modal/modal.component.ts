import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal.component.html'
})
export class ModalComponent {
  @Input() isOpen = false;
  @Input() title = 'Aviso';
  @Input() message = '';
  @Input() type: 'success' | 'error' | 'warning' | 'info' = 'info';
  @Input() confirmText = 'OK';
  @Input() cancelText = 'Cancelar';
  @Input() showCancel = false;
  
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  onConfirm() {
    this.isOpen = false;
    this.confirm.emit();
  }

  onCancel() {
    this.isOpen = false;
    this.cancel.emit();
  }
}
