import { Component, inject, signal, HostListener, ViewChild, ElementRef } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './admin-layout.component.html',
  host: { class: 'flex flex-col flex-grow h-screen overflow-hidden' }
})
export class AdminLayoutComponent {
  protected readonly auth = inject(AuthService);
  protected readonly dropdownOpen = signal(false);
  protected readonly sidebarExpanded = signal(true);

  @ViewChild('dropdownContainer') dropdownContainer?: ElementRef;

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (this.dropdownOpen() && this.dropdownContainer) {
      const clickedInside = this.dropdownContainer.nativeElement.contains(event.target);
      if (!clickedInside) {
        this.dropdownOpen.set(false);
      }
    }
  }

  toggleDropdown(): void {
    this.dropdownOpen.update(v => !v);
  }

  toggleSidebar(): void {
    this.sidebarExpanded.update(v => !v);
  }
}
