import { isPlatformBrowser } from '@angular/common';
import {
  Component,
  inject,
  OnInit,
  PLATFORM_ID,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import {
  Router,
  RouterLink,
  RouterOutlet,
} from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    RouterOutlet, RouterLink,
    MatSidenavModule, MatListModule,
    MatIconModule, MatToolbarModule, MatButtonModule
  ],
  templateUrl: './layout.component.html',
})
export class LayoutComponent implements OnInit {
  private platformId = inject(PLATFORM_ID);
  role: string | null = null;

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('jwt_token');
      if (token) {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.role = payload.roles;
      }
    }
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}