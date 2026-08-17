import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DropdownComponent, DropdownOption } from '../../../shared/components/dropdown/dropdown.component';
import { Offer, OfferService } from '../../offers/offer.service';

@Component({
  selector: 'app-offers-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, DropdownComponent],
  templateUrl: './offers-admin.component.html',
})
export class OffersAdminComponent implements OnInit {
  private readonly offerService = inject(OfferService);
  
  offers = signal<Offer[]>([]);
  isLoading = signal(true);

  showModal = signal(false);
  isEditing = signal(false);
  
  // Form Model
  currentOffer = signal<Partial<Offer>>({});
  
  categoryOptions: DropdownOption[] = [
    { label: 'Jogos', value: 'Jogos' },
    { label: 'Consoles', value: 'Consoles' },
    { label: 'Hardware', value: 'Hardware' },
    { label: 'Colecionáveis', value: 'Colecionáveis' }
  ];

  storeOptions: DropdownOption[] = [
    { label: 'Amazon', value: 'Amazon' },
    { label: 'Mercado Livre', value: 'Mercado Livre' },
    { label: 'Shopee', value: 'Shopee' },
    { label: 'KaBuM!', value: 'KaBuM!' },
    { label: 'PlayStation Store', value: 'PlayStation Store' }
  ];

  ngOnInit() {
    this.loadOffers();
  }

  loadOffers() {
    this.isLoading.set(true);
    this.offerService.getAdminOffers(0, 100).subscribe({
      next: (page) => {
        this.offers.set(page.content);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar ofertas:', err);
        this.isLoading.set(false);
      }
    });
  }

  openCreateModal() {
    this.isEditing.set(false);
    this.currentOffer.set({
      category: 'Jogos',
      storeName: 'Amazon',
      isFlashDeal: false,
      isActive: true,
      originalPrice: 0,
      discountPrice: 0
    });
    this.showModal.set(true);
  }

  openEditModal(offer: Offer) {
    this.isEditing.set(true);
    this.currentOffer.set({ ...offer });
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  calculateDiscount() {
    const offer = this.currentOffer();
    if (offer.originalPrice && offer.discountPrice) {
      const discount = ((offer.originalPrice - offer.discountPrice) / offer.originalPrice) * 100;
      this.currentOffer.set({ ...offer, discountPercentage: Math.round(discount) });
    }
  }

  formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null || isNaN(value) || value === 0) return '';
    return value.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  parseCurrency(value: string): number {
    if (!value) return 0;
    const digits = value.replace(/\D/g, '');
    return Number(digits) / 100;
  }

  onOriginalPriceChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const num = this.parseCurrency(input.value);
    this.currentOffer.update(o => ({ ...o, originalPrice: num }));
    input.value = this.formatCurrency(num);
    this.calculateDiscount();
  }

  onDiscountPriceChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const num = this.parseCurrency(input.value);
    this.currentOffer.update(o => ({ ...o, discountPrice: num }));
    input.value = this.formatCurrency(num);
    this.calculateDiscount();
  }

  saveOffer() {
    const offerData = this.currentOffer();
    
    if (this.isEditing() && offerData.id) {
      this.offerService.updateOffer(offerData.id, offerData).subscribe({
        next: () => {
          this.loadOffers();
          this.closeModal();
        },
        error: (err) => console.error('Erro ao atualizar oferta:', err)
      });
    } else {
      this.offerService.createOffer(offerData).subscribe({
        next: () => {
          this.loadOffers();
          this.closeModal();
        },
        error: (err) => console.error('Erro ao criar oferta:', err)
      });
    }
  }

  deleteOffer(id: string) {
    if (confirm('Tem certeza que deseja excluir esta oferta?')) {
      this.offerService.deleteOffer(id).subscribe({
        next: () => this.loadOffers(),
        error: (err) => console.error('Erro ao deletar oferta:', err)
      });
    }
  }

  toggleActive(offer: Offer) {
    const updatedOffer = { ...offer, isActive: !offer.isActive };
    this.offerService.updateOffer(offer.id, updatedOffer).subscribe({
      next: () => this.loadOffers(),
      error: (err) => console.error('Erro ao alternar status da oferta:', err)
    });
  }
}
