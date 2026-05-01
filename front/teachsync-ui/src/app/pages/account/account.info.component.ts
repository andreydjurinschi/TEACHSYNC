import { Component, inject, OnInit, signal } from "@angular/core";
import { ProfileInfo } from "../../core/models/profile/profile-info.model";
import { ActivatedRoute } from "@angular/router";
import { ProfileService } from "../../core/services/profile.service";
import { CommonModule } from "@angular/common";
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { UserService } from "../../core/services/user.service";
import { EditAccount } from "../../core/models/profile/pofile-editor.model";

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './account-info.html'
})
export class AccountInfo implements OnInit {
  user = signal<ProfileInfo | null>(null);
  loading = signal(false);
  saving = signal(false);
  dropdownOpen = signal(false);
  editMode = signal(false);
  previewUrl = signal<string | null>(null);
  imageError = signal<string | null>(null);
  editForm!: FormGroup;

  private route = inject(ActivatedRoute);
  private profileService = inject(ProfileService);
  private userService = inject(UserService);
  private fb = inject(FormBuilder);

  ngOnInit(): void {
    const emailFromQuery = this.route.snapshot.queryParamMap.get('email');
    const email = emailFromQuery ?? this.getEmailFromToken();
    if (email) this.loadProfile(email);
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
      },
      error: err => {
        console.error(err);
        this.loading.set(false);
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
