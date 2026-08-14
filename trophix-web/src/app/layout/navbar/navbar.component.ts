import { Component, inject, signal, HostListener, ElementRef, ViewChild } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styles: [
    `
      .nav-link-active {
        color: #a78bfa;
        box-shadow: inset 0 -2px 0 0 #8b5cf6;
      }
    `,
  ],
})
export class NavbarComponent {
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly dropdownOpen = signal(false);
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
    this.dropdownOpen.update((open) => !open);
  }

  async onLogout(): Promise<void> {
    this.dropdownOpen.set(false);
    await this.auth.logout();
    await this.router.navigate(['/login']);
  }
}
