import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Offer, OfferService } from './offer.service';

@Component({
  selector: 'app-offers',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './offers.component.html',
})
export class OffersComponent implements OnInit {
  private readonly offerService = inject(OfferService);
  
  // Categorias disponíveis para filtro
  categories = signal(['Todos', 'Jogos', 'Consoles', 'Hardware', 'Colecionáveis']);
  selectedCategory = signal('Todos');

  // Dados carregados da API
  offers = signal<Offer[]>([]);
  isLoading = signal(true);

  ngOnInit() {
    this.loadOffers();
  }

  loadOffers() {
    this.isLoading.set(true);
    // Aqui assumimos que queremos trazer uns 50 resultados iniciais. Depois pode implementar paginação com scroll.
    this.offerService.getPublicOffers(0, 50, this.selectedCategory()).subscribe({
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

  get filteredOffers() {
    return this.offers();
  }

  setCategory(category: string) {
    this.selectedCategory.set(category);
    this.loadOffers();
  }

  trackClick(offerId: string) {
    this.offerService.trackClick(offerId).subscribe({
      error: (err) => console.error('Silent track failure:', err) // Falha silenciosa para não impactar a navegação
    });
  }

  getValidUrl(url: string | undefined): string {
    if (!url) return '#';
    if (!url.startsWith('http://') && !url.startsWith('https://')) {
      return 'https://' + url;
    }
    return url;
  }
}
