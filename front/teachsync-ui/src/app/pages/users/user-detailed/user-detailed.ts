import { CommonModule } from "@angular/common";
import { Component, OnInit, signal } from "@angular/core";
import { User } from "../../../core/models/users/user.model";
import { UserService } from "../../../core/services/user.service";
import { ActivatedRoute } from "@angular/router";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { UserWithCourses } from "../../../core/models/users/user.detailed.model";

@Component({
  selector: 'app-user-detailed',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule],
  templateUrl: './user-detailed.html',
})
export class UserDetailed implements OnInit {

  user = signal<User | null>(null);
  userWithCourses = signal<UserWithCourses | null>(null);
  loading = signal<boolean>(false);

  constructor(
    private route: ActivatedRoute,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    const userId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadUser(userId);
  }

  loadUser(id: number): void {
    this.loading.set(true);

    this.userService.getById(id).subscribe({
      next: user => {
        this.user.set(user);

        if (user.role === 'TEACHER') {
          // Запрос курсов через сервис
          this.userService.getWithCourses(user.id).subscribe({
            next: userWithCourses => {
              this.userWithCourses.set(userWithCourses);
            },
            error: () => {
              // fallback если сервис курсов недоступен
              this.userWithCourses.set({
                name: user.fullName.split(' ')[0] || user.fullName,
                surname: user.fullName.split(' ')[1] || '',
                email: user.email,
                courseNames: [],
                available: false
              });
            },
            complete: () => this.loading.set(false)
          });
        } else {
          // Для ADMIN / MANAGER — только базовая инфа
          this.userWithCourses.set({
            name: user.fullName.split(' ')[0] || user.fullName,
            surname: user.fullName.split(' ')[1] || '',
            email: user.email,
            courseNames: [],
            available: false
          });
          this.loading.set(false);
        }
      },
      error: () => {
        this.user.set(null);
        this.userWithCourses.set(null);
        this.loading.set(false);
      }
    });
  }
}
