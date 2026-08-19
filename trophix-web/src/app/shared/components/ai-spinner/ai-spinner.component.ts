import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-ai-spinner',
  standalone: true,
  template: `
    @if (isVisible) {
      <div class="fixed inset-0 z-[100] flex items-center justify-center bg-slate-950/80 backdrop-blur-sm">
        <div class="flex flex-col items-center justify-center rounded-2xl border border-violet-500/30 bg-slate-900 p-8 shadow-2xl shadow-violet-900/20">
          <div class="relative flex h-16 w-16 items-center justify-center">
            <div class="absolute h-full w-full animate-spin rounded-full border-4 border-violet-500/20 border-t-violet-500"></div>
            <div class="h-8 w-8 animate-pulse rounded-full bg-violet-500"></div>
          </div>
          <h3 class="mt-6 text-xl font-bold text-white">✨ {{ title }}</h3>
          <p class="mt-2 max-w-[280px] text-center text-sm text-slate-400">{{ message }}</p>
        </div>
      </div>
    }
  `
})
export class AiSpinnerComponent {
  @Input() isVisible = false;
  @Input() title = 'IA Gerando Conteúdo...';
  @Input() message = 'O Gemini está escrevendo o conteúdo. Isso pode levar até 90 segundos.';
}
