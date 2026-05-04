import { CommonModule } from '@angular/common';
import { Component, OnInit, signal, computed, effect } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { UserWithCourses } from '../../../core/models/users/user.detailed.model';
import { User } from '../../../core/models/users/user.model';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { TeacherService } from '../../../core/services/teacher.service';
import { CategoryBase } from '../../../core/models/category/category.model';
import { PaginationControlsComponent } from '../../../shared/pagination/pagination-controls.component';
import { getTotalPages, paginateItems } from '../../../shared/pagination/pagination.utils';

@Component({
  selector: 'app-user-detailed',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule, RouterLink, PaginationControlsComponent],
  templateUrl: './user-detailed.html',
})
export class UserDetailed implements OnInit {

  user             = signal<User | null>(null);
  userWithCourses  = signal<UserWithCourses | null>(null);
  specializations  = signal<CategoryBase[]>([]);   // ← новый сигнал
  loading          = signal(false);
  selectedCourseId = signal<number | null>(null);
  currentPage = signal(1);
  private readonly pageSize = 6;

  currentEmail = computed(() => {
    const token = this.authService.getToken();
    if (!token) return null;
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload?.sub ?? null;
  });

  isSelf = computed(() => this.currentEmail() === this.userWithCourses()?.email);
  totalPages = computed(() => getTotalPages(this.courseNames().length, this.pageSize));
  visibleCourses = computed(() => paginateItems(this.courseNames(), this.currentPage(), this.pageSize));
  courseNames = computed(() => this.userWithCourses()?.courseNames ?? []);

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private teacherService: TeacherService,   // ← inject
    private router: Router,
    private authService: AuthService
  ) {
    effect(() => {
      const maxPage = this.totalPages();
      if (this.currentPage() > maxPage) {
        this.currentPage.set(maxPage);
      }
    });
  }

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
          // загружаем специализации параллельно с курсами
          this.teacherService.getSpecializations(id).subscribe({
            next: specs => this.specializations.set(specs),
            error: ()  => this.specializations.set([])
          });

          this.userService.getWithCourses(user.id).subscribe({
            next: data => {
              this.userWithCourses.set(data);
              this.currentPage.set(1);
            },
            error: () => this.userWithCourses.set({
              id: user.id, name: user.name, surname: user.surname,
              email: user.email, courseNames: [], available: false
            }),
            complete: () => this.loading.set(false)
          });
        } else {
          this.userWithCourses.set({
            id: user.id, name: user.name, surname: user.surname,
            email: user.email, courseNames: [], available: false
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

  confirmDelete(): void {
    const id = this.userWithCourses()?.id;
    if (!id) return;
    if (!confirm(`Удалить пользователя ${this.userWithCourses()?.name} ${this.userWithCourses()?.surname}?`)) return;
    this.userService.delete(id).subscribe({
      next: () => this.router.navigate(['/users']),
      error: err => console.error(err)
    });
  }

  toggleCourse(id: number): void {
    this.selectedCourseId.set(this.selectedCourseId() === id ? null : id);
  }

  trackByCourseId(_: number, course: any) {
    return course.id;
  }
}
