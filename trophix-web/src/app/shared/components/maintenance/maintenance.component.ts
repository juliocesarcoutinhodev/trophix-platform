import { Component } from '@angular/core';

@Component({
  selector: 'app-maintenance',
  standalone: true,
  template: `
    <div class="fixed inset-0 z-[9999] flex flex-col items-center justify-center bg-zinc-950 text-white p-6 text-center">
      <div class="max-w-md w-full flex flex-col items-center">
        <!-- Icon -->
        <div class="mb-8 relative">
          <div class="absolute inset-0 bg-violet-500/20 blur-2xl rounded-full animate-pulse"></div>
          <div class="relative bg-zinc-900 border border-zinc-800 p-6 rounded-2xl shadow-xl shadow-black/50">
            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-violet-500 animate-[spin_4s_linear_infinite]">
              <path d="M12 2v4"></path>
              <path d="m16.2 7.8 2.9-2.9"></path>
              <path d="M18 12h4"></path>
              <path d="m16.2 16.2 2.9 2.9"></path>
              <path d="M12 18v4"></path>
              <path d="m4.9 19.1 2.9-2.9"></path>
              <path d="M2 12h4"></path>
              <path d="m4.9 4.9 2.9 2.9"></path>
            </svg>
          </div>
        </div>

        <h1 class="text-3xl font-bold mb-4 tracking-tight">Servidores Offline</h1>
        
        <p class="text-zinc-400 mb-8 leading-relaxed">
          Nossos servidores estão passando por uma atualização ou encontram-se temporariamente indisponíveis. 
          Não se preocupe, estamos trabalhando para voltar o mais rápido possível.
        </p>

        <div class="flex items-center gap-3 text-sm text-zinc-500 bg-zinc-900/50 border border-zinc-800/50 py-3 px-5 rounded-full">
          <div class="relative flex h-3 w-3">
            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-violet-400 opacity-75"></span>
            <span class="relative inline-flex rounded-full h-3 w-3 bg-violet-500"></span>
          </div>
          Tentando reconectar automaticamente...
        </div>
      </div>
    </div>
  `
})
export class MaintenanceComponent {}
