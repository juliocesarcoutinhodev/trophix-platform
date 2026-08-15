import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-maintenance',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="flex min-h-screen items-center justify-center bg-slate-950 px-4 py-16 sm:px-6 sm:py-24 md:grid-cols-2 lg:px-8">
      <div class="mx-auto max-w-max text-center">
        <main class="sm:flex">
          <p class="text-4xl font-extrabold text-violet-600 sm:text-5xl">503</p>
          <div class="sm:ml-6">
            <div class="sm:border-l sm:border-slate-800 sm:pl-6">
              <h1 class="text-4xl font-extrabold tracking-tight text-white sm:text-5xl">Em Manutenção</h1>
              <p class="mt-2 text-base text-slate-400">Nossos servidores estão passando por atualizações ou estão temporariamente indisponíveis.</p>
              <p class="mt-1 text-base text-slate-500">Estamos trabalhando para voltar o mais rápido possível. Obrigado pela paciência!</p>
            </div>
            <div class="mt-10 flex space-x-3 sm:border-l sm:border-transparent sm:pl-6 justify-center sm:justify-start">
              <a routerLink="/" class="inline-flex items-center rounded-md border border-transparent bg-violet-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-violet-700 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:ring-offset-2 focus:ring-offset-slate-950">
                Tentar Novamente
              </a>
              <a href="https://twitter.com/trophix" target="_blank" class="inline-flex items-center rounded-md border border-slate-700 bg-slate-800 px-4 py-2 text-sm font-medium text-slate-200 hover:bg-slate-700 focus:outline-none focus:ring-2 focus:ring-violet-500 focus:ring-offset-2 focus:ring-offset-slate-950">
                Acompanhar Status
              </a>
            </div>
          </div>
        </main>
      </div>
    </div>
  `
})
export class MaintenanceComponent {}
