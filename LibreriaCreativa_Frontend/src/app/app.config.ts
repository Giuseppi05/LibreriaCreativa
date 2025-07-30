import { 
  ApplicationConfig, 
  importProvidersFrom, 
  provideAppInitializer,
  provideBrowserGlobalErrorListeners, 
  provideZoneChangeDetection,
  inject
} from '@angular/core';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { provideToastr } from 'ngx-toastr';

import { routes } from './app.routes';
import { jwtInterceptor } from './services/jwtinterceptor';
import { AuthService } from './services/auth-service';

export function initAuthFactory() {
  const authService = inject(AuthService);
  return authService.initializeAuth();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideToastr({
      enableHtml: true,
      timeOut: 5000,
      positionClass: 'toast-bottom-left',
      progressBar: true,
      tapToDismiss: true,
      preventDuplicates: true,
    }),
    importProvidersFrom(BrowserAnimationsModule),
    provideAppInitializer(initAuthFactory),
  ],
};
