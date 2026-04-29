import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { NotificationItem } from '../../core/models/notifications/notification.model';
import { NotificationService } from '../../core/services/notification.service';
import { RuleService } from '../../core/services/role.rule.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './notifications.component.html',
})
export class NotificationsComponent implements OnInit {
  private notificationService = inject(NotificationService);
  private ruleService = inject(RuleService);

  notifications = signal<NotificationItem[]>([]);
  loading = signal(true);
  markingAll = signal(false);
  role = signal<string | null>(null);
  userId = signal<number | null>(null);
  error = signal<string | null>(null);

  unreadCount = computed(() => this.notifications().filter(item => !item.read).length);

  ngOnInit(): void {
    this.role.set(this.ruleService.getRole());
    this.userId.set(this.ruleService.getId());
    this.loadNotifications();
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
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Не удалось загрузить уведомления.');
        this.loading.set(false);
      }
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

  getRoleTitle(): string {
    switch (this.role()) {
      case 'ADMIN':
        return 'Уведомления администратора';
      case 'MANAGER':
        return 'Уведомления менеджера';
      case 'TEACHER':
        return 'Уведомления преподавателя';
      default:
        return 'Уведомления';
    }
  }

  getSubjectLabel(subject: string): string {
    const map: Record<string, string> = {
      COURSE_CREATED: 'Создание курса',
      COURSE_UPDATED: 'Изменение курса',
      COURSE_GROUP_ENROLLED: 'Привязка группы',
      COURSE_GROUP_REMOVED: 'Удаление связи курса и группы',
      COURSE_TOPIC_ADDED: 'Добавление темы',
      COURSE_TOPIC_REMOVED: 'Удаление темы',
      COURSE_TEACHER_UNASSIGNED: 'Курс без преподавателя',
      TEACHER_ASSIGNMENT_REQUESTED: 'Запрос преподавателю',
      SCHEDULE_CREATED: 'Создание расписания',
      TEACHER_ASSIGNED: 'Назначение преподавателя',
      REPLACEMENT_REQUESTED: 'Запрос замены',
      REPLACEMENT_APPROVED: 'Замена найдена',
      REPLACEMENT_STATUS_CHANGED: 'Изменение статуса замены',
      USER_CREATED: 'Создание пользователя',
      USER_DELETED: 'Удаление пользователя',
      USER_ROLE_CHANGED: 'Смена роли',
      USER_SPECIALIZATION_ADDED: 'Добавление специализации',
      USER_SPECIALIZATION_REMOVED: 'Удаление специализации'
    };

    return map[subject] ?? subject;
  }

  getSourceLabel(sourceService: string): string {
    switch (sourceService) {
      case 'course-service':
        return 'Course Service';
      case 'schedule-service':
        return 'Schedule Service';
      case 'replacement-service':
        return 'Replacement Service';
      case 'users-service':
      case 'user-service':
        return 'Users Service';
      default:
        return sourceService;
    }
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
