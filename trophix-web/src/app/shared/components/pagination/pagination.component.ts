import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: true,
  template: `
    <div class="flex items-center justify-center border-t border-slate-800 bg-slate-900/40 py-4 backdrop-blur-sm">
      <nav class="isolate inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination">
        <!-- First Page -->
        <button 
          [disabled]="currentPage() === 0"
          (click)="onPageChange(0)"
          class="relative inline-flex items-center rounded-l-md px-3 py-2 text-slate-400 ring-1 ring-inset ring-slate-700 hover:bg-slate-800 focus:z-20 focus:outline-offset-0 disabled:opacity-50 transition-colors">
          <span class="sr-only">Primeira Página</span>
          <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
          </svg>
        </button>
        
        <!-- Previous Page -->
        <button 
          [disabled]="currentPage() === 0"
          (click)="onPageChange(currentPage() - 1)"
          class="relative inline-flex items-center px-3 py-2 text-slate-400 ring-1 ring-inset ring-slate-700 hover:bg-slate-800 focus:z-20 focus:outline-offset-0 disabled:opacity-50 transition-colors">
          <span class="sr-only">Anterior</span>
          <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        
        <!-- Current Page Indicator -->
        <span class="relative inline-flex items-center px-4 py-2 text-sm font-semibold text-slate-200 ring-1 ring-inset ring-slate-700 bg-slate-800/50 focus:outline-offset-0">
          {{ totalPages() === 0 ? 0 : currentPage() + 1 }}
        </span>

        <!-- Next Page -->
        <button 
          [disabled]="currentPage() >= totalPages() - 1 || totalPages() === 0"
          (click)="onPageChange(currentPage() + 1)"
          class="relative inline-flex items-center px-3 py-2 text-slate-400 ring-1 ring-inset ring-slate-700 hover:bg-slate-800 focus:z-20 focus:outline-offset-0 disabled:opacity-50 transition-colors">
          <span class="sr-only">Próxima</span>
          <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>

        <!-- Last Page -->
        <button 
          [disabled]="currentPage() >= totalPages() - 1 || totalPages() === 0"
          (click)="onPageChange(totalPages() - 1)"
          class="relative inline-flex items-center rounded-r-md px-3 py-2 text-slate-400 ring-1 ring-inset ring-slate-700 hover:bg-slate-800 focus:z-20 focus:outline-offset-0 disabled:opacity-50 transition-colors">
          <span class="sr-only">Última Página</span>
          <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 5l7 7-7 7M5 5l7 7-7 7" />
          </svg>
        </button>
      </nav>
    </div>
  `
})
export class PaginationComponent {
  currentPage = input.required<number>();
  totalPages = input.required<number>();
  totalElements = input.required<number>();
  
  pageChange = output<number>();

  onPageChange(page: number) {
    if (page >= 0 && page < this.totalPages()) {
      this.pageChange.emit(page);
    }
  }
}
