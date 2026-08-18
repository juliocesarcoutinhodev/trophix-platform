import { ApplicationConfig, provideBrowserGlobalErrorListeners, LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAppInitializer } from '@angular/core';
import { provideRouter, withInMemoryScrolling } from '@angular/router';
import { inject } from '@angular/core';
import { provideMarkdown } from 'ngx-markdown';

import { routes } from './app.routes';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';
import { AuthService } from './core/services/auth.service';

registerLocaleData(localePt);

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(
      routes,
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
        anchorScrolling: 'enabled',
      })
    ),
    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
    provideMarkdown(),
    provideAppInitializer(() => inject(AuthService).initialize()),
    { provide: LOCALE_ID, useValue: 'pt-BR' }
  ],
};
