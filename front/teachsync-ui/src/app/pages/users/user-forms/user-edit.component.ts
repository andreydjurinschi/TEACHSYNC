import { Component, OnInit, signal, computed } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

import { User } from '../../../core/models/users/user.model';
import { UserRole } from '../../../core/models/users/user.role.model';
import { UserService } from '../../../core/services/user.service';
import { CategoryService } from '../../../core/services/category.service';
import { TeacherService } from '../../../core/services/teacher.service';
import { CategoryBase } from '../../../core/models/category/category.model';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit-form.html',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule, CommonModule],
})
export class UserEdit implements OnInit {
  user        = signal<User | null>(null);
  loading     = signal(false);
  saving      = signal(false);
  categories  = signal<CategoryBase[]>([]);
  currentSpecIds = signal<Set<number>>(new Set());
  selectedSpecIds = signal<Set<number>>(new Set());

  isTeacher = computed(() => this.form.get('role')?.value === 'TEACHER');

  form;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private userService: UserService,
    private categoryService: CategoryService,
    private teacherService: TeacherService,
    private router: Router,
  ) {
    this.form = this.fb.nonNullable.group({
      name:    ['', Validators.required],
      surname: ['', Validators.required],
      email:   ['', [Validators.required, Validators.email]],
      role:    ['TEACHER' as UserRole, Validators.required],
    });
  }

  ngOnInit(): void {
    const userId = Number(this.route.snapshot.paramMap.get('id'));
    this.categoryService.getAll().subscribe(d => this.categories.set(d));
    this.loadUser(userId);
  }

  loadUser(id: number): void {
    this.loading.set(true);
    this.userService.getById(id).subscribe({
      next: (user) => {
        this.user.set(user);
        this.form.patchValue({
          name:    user.name    ?? '',
          surname: user.surname ?? '',
          email:   user.email   ?? '',
          role:    user.role    ?? 'TEACHER',
        });
        if (user.role === 'TEACHER') {
          this.teacherService.getSpecializations(id).subscribe(specs => {
            const ids = new Set<number>(specs.map((s: any) => s.id));
            this.currentSpecIds.set(ids);
            this.selectedSpecIds.set(new Set(ids)); 
          });
        }
      },
      error: (err) => console.error(err),
      complete: () => this.loading.set(false),
    });
  }

  toggleSpec(id: number): void {
    const current = new Set(this.selectedSpecIds());
    current.has(id) ? current.delete(id) : current.add(id);
    this.selectedSpecIds.set(current);
  }

  isSpecSelected(id: number): boolean {
    return this.selectedSpecIds().has(id);
  }

  submit(): void {
    if (this.form.invalid || !this.user()) return;
    this.saving.set(true);

    const id = this.user()!.id;

    this.userService.update(id, this.form.value).subscribe({
      next: () => {
        if (this.form.value.role === 'TEACHER') {
          this.syncSpecializations(id);
        } else {
          this.saving.set(false);
          this.router.navigate(['/users', id]);
        }
      },
      error: (err) => {
        console.error(err);
        this.saving.set(false);
      },
    });
  }

  private syncSpecializations(teacherId: number): void {
    const prev = this.currentSpecIds();
    const next = this.selectedSpecIds();

    const toAdd    = [...next].filter(id => !prev.has(id));
    const toRemove = [...prev].filter(id => !next.has(id));

    const addRequests    = toAdd.map(catId =>
      this.teacherService.addSpecialization(teacherId, catId)
    );
    const removeRequests = toRemove.map(catId =>
      this.teacherService.removeSpecialization(teacherId, catId)
    );

    const all = [...addRequests, ...removeRequests];

    if (!all.length) {
      this.saving.set(false);
      this.router.navigate(['/users', teacherId]);
      return;
    }

    import('rxjs').then(({ forkJoin }) => {
      forkJoin(all).subscribe({
        next: () => {
          this.saving.set(false);
          this.router.navigate(['/users', teacherId]);
        },
        error: (err) => {
          console.error(err);
          this.saving.set(false);
        },
      });
    });
  }
}