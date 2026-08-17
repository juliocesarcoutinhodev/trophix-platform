import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DropdownComponent, DropdownOption } from '../../../shared/components/dropdown/dropdown.component';

interface MockOffer {
  id: string;
  title: string;
  imageUrl: string;
  originalPrice: number;
  discountPrice: number;
  discountPercentage: number;
  storeName: string;
  affiliateLink: string;
  category: string;
  isFlashDeal: boolean;
  isActive: boolean;
}

@Component({
  selector: 'app-offers-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, DropdownComponent],
  templateUrl: './offers-admin.component.html',
})
export class OffersAdminComponent {
  
  offers = signal<MockOffer[]>([
    {
      id: '1',
      title: 'Console PlayStation 5 Edição Digital',
      imageUrl: 'https://images.unsplash.com/photo-1606813907291-d86efa9b94db?q=80&w=800&auto=format&fit=crop',
      originalPrice: 3999.99,
      discountPrice: 3499.00,
      discountPercentage: 13,
      storeName: 'Amazon',
      affiliateLink: '#',
      category: 'Consoles',
      isFlashDeal: true,
      isActive: true
    },
    {
      id: '2',
      title: 'Controle DualSense - Midnight Black',
      imageUrl: 'https://images.unsplash.com/photo-1622290291468-a28f7a7dc6a8?q=80&w=800&auto=format&fit=crop',
      originalPrice: 469.90,
      discountPrice: 379.00,
      discountPercentage: 19,
      storeName: 'Mercado Livre',
      affiliateLink: '#',
      category: 'Hardware',
      isFlashDeal: false,
      isActive: true
    }
  ]);

  showModal = signal(false);
  isEditing = signal(false);
  
  // Form Model
  currentOffer = signal<Partial<MockOffer>>({});
  
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

  openCreateModal() {
    this.isEditing.set(false);
    this.currentOffer.set({
      category: 'Jogos',
      storeName: 'Amazon',
      isFlashDeal: false,
      isActive: true,
      discountPercentage: 0
    });
    this.showModal.set(true);
  }

  openEditModal(offer: MockOffer) {
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
    const offerData = this.currentOffer() as MockOffer;
    
    if (this.isEditing()) {
      this.offers.update(items => items.map(o => o.id === offerData.id ? offerData : o));
    } else {
      offerData.id = Math.random().toString(36).substr(2, 9);
      this.offers.update(items => [offerData, ...items]);
    }
    
    this.closeModal();
  }

  deleteOffer(id: string) {
    if (confirm('Tem certeza que deseja excluir esta oferta?')) {
      this.offers.update(items => items.filter(o => o.id !== id));
    }
  }

  toggleActive(offer: MockOffer) {
    const updatedOffer = { ...offer, isActive: !offer.isActive };
    this.offers.update(items => items.map(o => o.id === offer.id ? updatedOffer : o));
  }
}
