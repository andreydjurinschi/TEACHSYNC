import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { App } from './app/app';
import { routes } from './app/app.routes';
import { JWT_OPTIONS, JwtHelperService } from '@auth0/angular-jwt';

bootstrapApplication(App, {
  providers: [
    provideRouter(routes),
    provideHttpClient(),    
    { provide: JWT_OPTIONS, useValue: JWT_OPTIONS },
    JwtHelperService
  ]
});
