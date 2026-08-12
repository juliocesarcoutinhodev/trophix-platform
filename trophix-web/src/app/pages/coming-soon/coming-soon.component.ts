import { Component, input } from '@angular/core';

@Component({
  selector: 'app-coming-soon',
  standalone: true,
  template: `
    <main class="mx-auto flex min-h-[60vh] max-w-3xl flex-col items-center justify-center px-4 pt-16 text-center">
      <div class="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-violet-600/15">
        <svg class="h-7 w-7 text-violet-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-slate-100">{{ title() }}</h1>
      <p class="mt-2 text-slate-400">Em breve. Esta seção está em construção.</p>
    </main>
  `,
})
export class ComingSoonComponent {
  readonly title = input('Em construção');
}
