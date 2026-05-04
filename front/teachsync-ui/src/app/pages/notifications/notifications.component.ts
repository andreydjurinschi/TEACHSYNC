import { CommonModule } from '@angular/common';
import { Component, computed, DestroyRef, effect, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { NotificationItem, NotificationPreference } from '../../core/models/notifications/notification.model';
import { NotificationService } from '../../core/services/notification.service';
import { RuleService } from '../../core/services/role.rule.service';
import { PaginationControlsComponent } from '../../shared/pagination/pagination-controls.component';
import { getTotalPages, paginateItems } from '../../shared/pagination/pagination.utils';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, RouterLink, PaginationControlsComponent],
  templateUrl: './notifications.component.html',
})
export class NotificationsComponent implements OnInit {
  private notificationService = inject(NotificationService);
  private ruleService = inject(RuleService);
  private destroyRef = inject(DestroyRef);

  notifications = signal<NotificationItem[]>([]);
  loading = signal(true);
  markingAll = signal(false);
  role = signal<string | null>(null);
  userId = signal<number | null>(null);
  error = signal<string | null>(null);
  filter = signal<'all' | 'unread' | 'read'>('all');
  preferences = signal<NotificationPreference | null>(null);
  preferencesSaving = signal(false);
  currentPage = signal(1);
  private readonly pageSize = 8;

  unreadCount = computed(() => this.notifications().filter(item => !item.read).length);
  readCount = computed(() => this.notifications().filter(item => item.read).length);
  filteredNotifications = computed(() => {
    switch (this.filter()) {
      case 'unread':
        return this.notifications().filter(item => !item.read);
      case 'read':
        return this.notifications().filter(item => item.read);
      default:
        return this.notifications();
    }
  });
  totalPages = computed(() => getTotalPages(this.filteredNotifications().length, this.pageSize));
  visibleNotifications = computed(() => paginateItems(this.filteredNotifications(), this.currentPage(), this.pageSize));

  constructor() {
    effect(() => {
      const maxPage = this.totalPages();
      if (this.currentPage() > maxPage) {
        this.currentPage.set(maxPage);
      }
    });
  }

  ngOnInit(): void {
    this.role.set(this.ruleService.getRole());
    this.userId.set(this.ruleService.getId());
    this.loadNotifications();
    this.loadPreferences();
    this.listenRealtime();
  }

  loadNotifications(): void {
    const role = this.role();
    const userId = this.userId();

    if (!role || userId == null) {
      this.error.set('Не удалось определить текущего пользователя.');
      this.loading.set(false);
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    const request = role === 'TEACHER'
      ? this.notificationService.getForUser(userId)
      : this.notificationService.getForRole(role, userId);

    request.subscribe({
      next: data => {
        this.notifications.set(data);
        this.currentPage.set(1);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Не удалось загрузить уведомления.');
        this.loading.set(false);
      }
    });
  }

  loadPreferences(): void {
    const userId = this.userId();
    if (userId == null) return;

    this.notificationService.getPreferences(userId).subscribe({
      next: preferences => this.preferences.set(preferences),
      error: () => this.error.set('Не удалось загрузить настройки уведомлений.')
    });
  }

  updatePreference(key: keyof Omit<NotificationPreference, 'userId'>, event: Event): void {
    const current = this.preferences();
    const userId = this.userId();
    if (!current || userId == null) return;

    const checked = (event.target as HTMLInputElement).checked;
    const updated = { ...current, [key]: checked };

    this.preferences.set(updated);
    this.preferencesSaving.set(true);
    this.notificationService.updatePreferences(userId, updated).subscribe({
      next: preferences => {
        this.preferences.set(preferences);
        this.preferencesSaving.set(false);
        this.notificationService.triggerRefresh();
        this.loadNotifications();
      },
      error: () => {
        this.preferences.set(current);
        this.preferencesSaving.set(false);
        this.error.set('Не удалось сохранить настройки уведомлений.');
      }
    });
  }

  private listenRealtime(): void {
    this.notificationService.realtime$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(notification => {
        this.notifications.update(list => {
          if (list.some(item => item.id === notification.id)) {
            return list;
          }
          return [{ ...notification, read: false }, ...list];
        });
        this.currentPage.set(1);
      });
  }

  markAsRead(item: NotificationItem): void {
    const userId = this.userId();
    if (item.read || userId == null) return;

    this.notificationService.markAsRead(item.id, userId).subscribe({
      next: () => {
        this.notifications.update(list =>
          list.map(notification =>
            notification.id === item.id ? { ...notification, read: true } : notification
          )
        );
        this.notificationService.triggerRefresh();
      }
    });
  }

  markAllAsRead(): void {
    const role = this.role();
    const userId = this.userId();
    if (!role || userId == null || this.unreadCount() === 0) return;

    this.markingAll.set(true);

    if (role !== 'TEACHER') {
      this.notificationService.markAllReadForRole(role, userId).subscribe({
        next: () => {
          this.notifications.update(list => list.map(item => ({ ...item, read: true })));
          this.markingAll.set(false);
          this.notificationService.triggerRefresh();
        },
        error: () => {
          this.markingAll.set(false);
        }
      });
      return;
    }

    const unreadIds = this.notifications()
      .filter(item => !item.read)
      .map(item => item.id);

    if (!unreadIds.length) {
      this.markingAll.set(false);
      return;
    }

    forkJoin(unreadIds.map(id => this.notificationService.markAsRead(id, userId))).subscribe({
      next: () => {
        this.notifications.update(list => list.map(item => ({ ...item, read: true })));
        this.markingAll.set(false);
        this.notificationService.triggerRefresh();
      },
      error: () => {
        this.markingAll.set(false);
      }
    });
  }

  trackById(_: number, item: NotificationItem): number {
    return item.id;
  }

  getActionPath(actionUrl: string): string {
    return actionUrl.split('?')[0];
  }

  getActionQueryParams(actionUrl: string): Record<string, string> | null {
    const query = actionUrl.split('?')[1];
    if (!query) return null;
    return Object.fromEntries(new URLSearchParams(query).entries());
  }
}
