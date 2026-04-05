import { CommonModule } from '@angular/common';
import {
  Component,
  OnInit,
  signal,
} from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import {
  ActivatedRoute,
  Router,
  RouterLink,
} from '@angular/router';

import {
  UserWithCourses,
} from '../../../core/models/users/user.detailed.model';
import { User } from '../../../core/models/users/user.model';
import { UserService } from '../../../core/services/user.service';

@Component({
  selector: 'app-user-detailed',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule, RouterLink],
  templateUrl: './user-detailed.html',
})
export class UserDetailed implements OnInit {

  user = signal<User | null>(null);
  userWithCourses = signal<UserWithCourses | null>(null);
  loading = signal<boolean>(false);

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    const userId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadUser(userId);
  }

  confirmDelete(): void {
  const id = this.userWithCourses()?.id;
  if (!id) return;

  const confirmed = confirm(`Удалить пользователя ${this.userWithCourses()?.name} ${this.userWithCourses()?.surname}?`);
  if (!confirmed) return;

  this.userService.delete(id).subscribe({
    next: () => this.router.navigate(['/users']),
    error: err => console.error('Ошибка при удалении:', err)
  });
}

  loadUser(id: number): void {
    this.loading.set(true);

    this.userService.getById(id).subscribe({
      next: user => {
        this.user.set(user);

        if (user.role === 'TEACHER') {

          this.userService.getWithCourses(user.id).subscribe({
            next: userWithCourses => {
              this.userWithCourses.set(userWithCourses);
            },
            error: () => {

              this.userWithCourses.set({
                id: user.id,
                name: user.name,
                surname: user.surname,
                email: user.email,
                courseNames: [],
                available: false
              });
            },
            complete: () => this.loading.set(false)
          });
        } else {
          
          this.userWithCourses.set({
            id: user.id,
            name: user.name,
            surname: user.surname,
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
