import { Component, inject, OnInit, signal } from "@angular/core";
import { ProfileInfo } from "../../core/models/profile/profile-info.model";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { ProfileService } from "../../core/services/profile.service";
import { CommonModule } from "@angular/common";

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './account-info.html'
})
export class AccountInfo implements OnInit {
  user = signal<ProfileInfo | null>(null);
  loading = signal(false);

  private route = inject(ActivatedRoute);
  private profileService = inject(ProfileService);

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