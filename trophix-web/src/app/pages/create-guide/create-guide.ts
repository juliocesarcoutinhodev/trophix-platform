import { Component, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { marked } from 'marked';

@Component({
  selector: 'app-create-guide',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './create-guide.html',
})
export class CreateGuide {
  title = signal('');
  description = signal('');
  videoUrl = signal('');
  content = signal('');
  activeTab = signal<'write' | 'preview'>('write');

  // Converte o Markdown em HTML puro toda vez que o content mudar
  parsedContent = computed(() => {
    let raw = this.content();
    if (!raw.trim()) return '';
    
    // Remove blocos de crase se o usuário copiar acidentalmente de uma IA
    if (raw.startsWith('```markdown')) {
      raw = raw.replace(/^```markdown\n?/, '').replace(/\n?```$/, '');
    }
    
    return marked.parse(raw) as string;
  });

  saveGuide() {
    // Integraremos com a API refatorada aqui no futuro
    console.log('Salvar guia', {
      title: this.title(),
      description: this.description(),
      videoUrl: this.videoUrl(),
      content: this.content()
    });
  }
}
