import { Component, DestroyRef, inject, OnInit, signal } from "@angular/core";
import { ProfileInfo } from "../../core/models/profile/profile-info.model";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { ProfileService } from "../../core/services/profile.service";
import { CommonModule } from "@angular/common";
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { UserService } from "../../core/services/user.service";
import { EditAccount } from "../../core/models/profile/pofile-editor.model";
import { NotificationService } from "../../core/services/notification.service";
import { UserActivity } from "../../core/models/notifications/notification.model";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './account-info.html'
})
export class AccountInfo implements OnInit {
  user = signal<ProfileInfo | null>(null);
  activities = signal<UserActivity[]>([]);
  loading = signal(false);
  activityLoading = signal(false);
  saving = signal(false);
  dropdownOpen = signal(false);
  editMode = signal(false);
  previewUrl = signal<string | null>(null);
  imageError = signal<string | null>(null);
  editForm!: FormGroup;

  private route = inject(ActivatedRoute);
  private profileService = inject(ProfileService);
  private userService = inject(UserService);
  private notificationService = inject(NotificationService);
  private destroyRef = inject(DestroyRef);
  private fb = inject(FormBuilder);

  ngOnInit(): void {
    const emailFromQuery = this.route.snapshot.queryParamMap.get('email');
    const email = emailFromQuery ?? this.getEmailFromToken();
    if (email) this.loadProfile(email);

    this.notificationService.realtime$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        const u = this.user();
        if (u) {
          this.loadActivities(u.id, u.role);
        }
      });
  }

  private getEmailFromToken(): string | null {
    try {
      const token = localStorage.getItem('jwt_token');
      if (!token) return null;
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload['email']
    } catch {
      return null;
    }
  }

  loadProfile(email: string): void {
    this.loading.set(true);
    this.profileService.getProfileInfo(email).subscribe({
      next: data => {
        this.user.set(data);
        this.loading.set(false);
        this.loadActivities(data.id, data.role);
      },
      error: err => {
        console.error(err);
        this.loading.set(false);
      }
    });
  }

  loadActivities(userId: number, role: string): void {
    this.activityLoading.set(true);
    this.notificationService.getActivities(userId, role, 10).subscribe({
      next: activities => {
        this.activities.set(activities);
        this.activityLoading.set(false);
      },
      error: err => {
        console.error(err);
        this.activities.set([]);
        this.activityLoading.set(false);
      }
    });
  }

  private getUserId(): number | null {
    return this.user()?.id ?? null;
  }

toggleEdit(): void {
  if (!this.editMode()) {
    const u = this.user();
    if (!u) return;
    this.editForm = this.fb.group({
      name:           [u.name ?? '', Validators.required],
      surname:        [u.surname ?? '', Validators.required],
      email:          [u.email ?? '', [Validators.required, Validators.email]],
      password:       [''],
      profilePicture: [u.profilePicture ?? ''],
    });
    this.previewUrl.set(u.profilePicture ?? null);
    this.imageError.set(null);
  }
  this.editMode.update(v => !v);
}

  saveEdit(): void {
    if (this.editForm.invalid) return;
    const id = this.getUserId();
    if (id == null) return;

    const raw = this.editForm.value;
    const payload: Partial<EditAccount> = {
      name: raw.name,
      surname: raw.surname,
      email: raw.email,
      profilePicture: raw.profilePicture ?? null,
    };
    if (raw.password?.trim()) payload.password = raw.password;

    this.saving.set(true);
    this.userService.editAccount(id, payload).subscribe({
      next: () => {
        this.saving.set(false);
        this.editMode.set(false);
        this.loadProfile(raw.email);
      },
      error: err => {
        console.error(err);
        this.saving.set(false);
      }
    });
  }

  getRoleBadge(role: string): string {
    const map: Record<string, string> = {
      ADMIN:   'app-status-blue',
      MANAGER: 'app-status',
      TEACHER: 'app-status-green',
    };
    return map[role] ?? 'app-status';
  }

  getRoleLabel(role: string): string {
    const map: Record<string, string> = {
      ADMIN: 'Administrator', MANAGER: 'Manager', TEACHER: 'Teacher'
    };
    return map[role] ?? role;
  }

  getActivityTone(actionType: string): string {
    if (actionType.includes('SCHEDULE')) {
      return 'border-blue-200 bg-blue-50 text-blue-700 dark:border-blue-900/60 dark:bg-blue-950/40 dark:text-blue-200';
    }
    if (actionType.includes('REPLACEMENT')) {
      return 'border-emerald-200 bg-emerald-50 text-emerald-700 dark:border-emerald-900/60 dark:bg-emerald-950/40 dark:text-emerald-200';
    }
    if (actionType.includes('COURSE')) {
      return 'border-violet-200 bg-violet-50 text-violet-700 dark:border-violet-900/60 dark:bg-violet-950/40 dark:text-violet-200';
    }
    return 'border-slate-200 bg-slate-50 text-slate-600 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-300';
  }

  getActivityLabel(actionType: string): string {
    if (actionType.includes('SCHEDULE')) return 'Расписание';
    if (actionType.includes('REPLACEMENT')) return 'Замена';
    if (actionType.includes('COURSE')) return 'Курс';
    if (actionType.includes('USER')) return 'Аккаунт';
    return 'Событие';
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      this.imageError.set('Можно загружать только изображения.');
      input.value = '';
      return;
    }
    if (file.size > 2 * 1024 * 1024) {
      this.imageError.set('Максимальный размер изображения — 2 МБ.');
      input.value = '';
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const result = typeof reader.result === 'string' ? reader.result : null;
      this.editForm.patchValue({ profilePicture: result });
      this.previewUrl.set(result);
      this.imageError.set(null);
    };
    reader.onerror = () => this.imageError.set('Не удалось прочитать изображение.');
    reader.readAsDataURL(file);
  }

  clearProfilePicture(): void {
    this.editForm.patchValue({ profilePicture: '' });
    this.previewUrl.set(null);
    this.imageError.set(null);
  }
}
