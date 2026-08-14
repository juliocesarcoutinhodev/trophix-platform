import { Component, ElementRef, HostListener, input, model, signal, ViewChild } from '@angular/core';

export interface DropdownOption {
  label: string;
  value: string | number | null;
}

@Component({
  selector: 'app-dropdown',
  standalone: true,
  templateUrl: './dropdown.component.html',
})
export class DropdownComponent {
  options = input.required<DropdownOption[]>();
  
  // Two-way binding compatible, e.g. [(value)]="myValue" or (valueChange)="doSomething()"
  value = model<string | number | null>(null);

  protected readonly isOpen = signal(false);
  @ViewChild('container') container?: ElementRef;

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (this.isOpen() && this.container) {
      if (!this.container.nativeElement.contains(event.target)) {
        this.isOpen.set(false);
      }
    }
  }

  toggle(): void {
    this.isOpen.update(v => !v);
  }

  selectOption(optionValue: string | number | null): void {
    this.value.set(optionValue);
    this.isOpen.set(false);
  }

  get currentLabel(): string {
    const current = this.options().find(opt => opt.value === this.value());
    return current ? current.label : 'Selecione...';
  }
}
