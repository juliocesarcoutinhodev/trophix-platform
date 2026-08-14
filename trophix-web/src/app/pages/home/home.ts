import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home.html',
})
export class HomeComponent {
  // Mock Data (Esses dados virão do backend futuramente)
  settings = signal({
    heroTitle: 'Qual jogo você quer platinar hoje?',
    heroSubtitle: 'A maior comunidade de caçadores de platinas. Explore milhares de guias detalhados criados por jogadores.',
    heroImageUrl: 'https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=2070&auto=format&fit=crop',
    globalAlertEnabled: true,
    globalAlertText: '🔥 Participe do novo Desafio Semanal no nosso Discord e ganhe badges exclusivas!'
  });

  searchQuery = signal('');

  recentGuides = signal([
    {
      id: '1',
      gameName: 'Elden Ring',
      title: 'Elden Ring - Guia de Platina (100%)',
      author: 'Cout1nh030',
      upvotes: 342,
      imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co4jni.jpg'
    },
    {
      id: '2',
      gameName: 'God of War Ragnarök',
      title: 'Guia de Colecionáveis (Corvos, Artefatos, Baús Nornir)',
      author: 'KratosHunter',
      upvotes: 215,
      imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co5s5v.jpg'
    },
    {
      id: '3',
      gameName: 'Marvel\'s Spider-Man 2',
      title: 'Platina em 25 Horas - Passo a Passo Eficiente',
      author: 'WebSlinger',
      upvotes: 189,
      imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co69f1.jpg'
    },
    {
      id: '4',
      gameName: 'The Last of Us Part I',
      title: 'Dificuldade Sobrevivente e Todos os Pingentes',
      author: 'EllieFan',
      upvotes: 156,
      imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co5x99.jpg'
    }
  ]);

  trendingGames = signal([
    { name: 'Elden Ring', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co4jni.jpg', guidesCount: 12 },
    { name: 'Final Fantasy VII Rebirth', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co7i0e.jpg', guidesCount: 8 },
    { name: 'Helldivers 2', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co79i1.jpg', guidesCount: 24 },
    { name: 'Baldur\'s Gate 3', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co670h.jpg', guidesCount: 15 },
    { name: 'Cyberpunk 2077', imageUrl: 'https://images.igdb.com/igdb/image/upload/t_cover_big/co2mvt.jpg', guidesCount: 30 }
  ]);

  topHunters = signal([
    { rank: 1, username: 'Cout1nh030', platinums: 152, level: 320 },
    { rank: 2, username: 'Hakoom', platinums: 145, level: 310 },
    { rank: 3, username: 'PlatinumKing', platinums: 128, level: 295 },
    { rank: 4, username: 'TrophyLover', platinums: 110, level: 250 },
    { rank: 5, username: 'GamerGirl99', platinums: 98, level: 215 },
  ]);
}
