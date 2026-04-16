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
      return payload.sub ?? payload.email ?? null;
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
      profilePicture: raw.profilePicture || undefined,
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
      ADMIN:   'bg-red-100 dark:bg-red-900/30 text-red-600 dark:text-red-400',
      MANAGER: 'bg-indigo-100 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-400',
      TEACHER: 'bg-emerald-100 dark:bg-emerald-900/30 text-emerald-600 dark:text-emerald-400',
    };
    return map[role] ?? 'bg-slate-100 text-slate-500';
  }

  getRoleLabel(role: string): string {
    const map: Record<string, string> = {
      ADMIN: 'Administrator', MANAGER: 'Manager', TEACHER: 'Teacher'
    };
    return map[role] ?? role;
  }
}