import { CommonModule, isPlatformBrowser } from '@angular/common';
import {
  Component,
  DestroyRef,
  inject,
  OnInit,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import {
  interval,
  merge,
  startWith,
  switchMap,
} from 'rxjs';

import {
  Router,
  RouterLink,
  RouterOutlet,
} from '@angular/router';

import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { RuleService } from '../../core/services/role.rule.service';
import { ThemeService } from '../../core/services/theme.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    RouterOutlet, RouterLink, CommonModule,
    MatSidenavModule, MatListModule,
    MatIconModule, MatToolbarModule, MatButtonModule
  ],
  templateUrl: './layout.component.html',
})
export class LayoutComponent implements OnInit {
  private platformId = inject(PLATFORM_ID);
  private destroyRef = inject(DestroyRef);
  private themeService = inject(ThemeService);
  private notificationService = inject(NotificationService);
  private ruleService = inject(RuleService);

  role: string | null = null;
  unreadCount = signal(0);
  theme = this.themeService.theme;
  coursesOpen = false;
  sidebarOpen = signal(false);

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('jwt_token');
      if (token) {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.role = payload.roles;
        this.startNotificationPolling();
        this.startNotificationStream();
      }
    }
  }

  toggleTheme() {
    this.themeService.toggle();
  }

  toggleCourses() {
    this.coursesOpen = !this.coursesOpen;
  }

  toggleSidebar() {
    this.sidebarOpen.update(value => !value);
  }

  closeSidebar() {
    this.sidebarOpen.set(false);
  }

  private startNotificationPolling(): void {
    const role = this.ruleService.getRole();
    const userId = this.ruleService.getId();

    if (!role || userId == null) {
      this.unreadCount.set(0);
      return;
    }

    merge(
      interval(30000).pipe(startWith(0)),
      this.notificationService.refresh$
    ).pipe(
      switchMap(() => {
        if (role === 'TEACHER') {
          return this.notificationService.getUnreadCountForUser(userId);
        }
        return this.notificationService.getUnreadCountForRole(role, userId);
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: count => this.unreadCount.set(count),
      error: () => this.unreadCount.set(0)
    });
  }

  private startNotificationStream(): void {
    const role = this.ruleService.getRole();
    const userId = this.ruleService.getId();

    if (!role || userId == null || typeof EventSource === 'undefined') {
      return;
    }

    this.notificationService.connectRealtime(userId, role);
  }

  logout() {
    this.auth.logout();
    this.unreadCount.set(0);
    this.notificationService.disconnectRealtime();
    this.router.navigate(['/login']);
  }
}
