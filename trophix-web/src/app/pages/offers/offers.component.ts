import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

interface MockOffer {
  id: string;
  title: string;
  imageUrl: string;
  originalPrice: number;
  discountPrice: number;
  discountPercentage: number;
  storeName: string; // ex: Amazon, Mercado Livre
  affiliateLink: string;
  category: string;
  isFlashDeal: boolean;
}

@Component({
  selector: 'app-offers',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './offers.component.html',
})
export class OffersComponent {
  
  // Categorias disponíveis para filtro
  categories = signal(['Todos', 'Jogos', 'Consoles', 'Hardware', 'Colecionáveis']);
  selectedCategory = signal('Todos');

  // Dados mockados para visualização do layout
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
      isFlashDeal: true
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
      isFlashDeal: false
    },
    {
      id: '3',
      title: 'Headset Sem Fio PULSE 3D - Branco',
      imageUrl: 'https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?q=80&w=800&auto=format&fit=crop',
      originalPrice: 599.00,
      discountPrice: 449.90,
      discountPercentage: 25,
      storeName: 'Amazon',
      affiliateLink: '#',
      category: 'Hardware',
      isFlashDeal: false
    },
    {
      id: '4',
      title: 'Elden Ring - PlayStation 5',
      imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co4jni.jpg',
      originalPrice: 299.90,
      discountPrice: 199.90,
      discountPercentage: 33,
      storeName: 'Shopee',
      affiliateLink: '#',
      category: 'Jogos',
      isFlashDeal: true
    },
    {
      id: '5',
      title: 'SSD WD Black SN850X 1TB para PS5',
      imageUrl: 'https://images.unsplash.com/photo-1597849021665-38435d7966da?q=80&w=800&auto=format&fit=crop',
      originalPrice: 899.00,
      discountPrice: 649.00,
      discountPercentage: 28,
      storeName: 'KaBuM!',
      affiliateLink: '#',
      category: 'Hardware',
      isFlashDeal: false
    },
    {
      id: '6',
      title: 'Funko Pop! The Last of Us - Joel',
      imageUrl: 'https://images.unsplash.com/photo-1608518928424-d2e8bfa1ea94?q=80&w=800&auto=format&fit=crop',
      originalPrice: 159.90,
      discountPrice: 119.90,
      discountPercentage: 25,
      storeName: 'Amazon',
      affiliateLink: '#',
      category: 'Colecionáveis',
      isFlashDeal: false
    }
  ]);

  get filteredOffers() {
    if (this.selectedCategory() === 'Todos') {
      return this.offers();
    }
    return this.offers().filter(o => o.category === this.selectedCategory());
  }

  setCategory(category: string) {
    this.selectedCategory.set(category);
  }
}
