import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal, computed } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { CourseService } from '../../../core/services/course.service';
import { CategoryService } from '../../../core/services/category.service';
import { TeacherService } from '../../../core/services/teacher.service';
import { CategoryBase } from '../../../core/models/category/category.model';
import { CourseBase } from '../../../core/models/courses/course.model';
import { TeacherDto } from '../../../core/models/users/teacher.model';
import { RuleService } from '../../../core/services/role.rule.service';

type TeacherFilter = 'none' | 'by-category' | 'all';

@Component({
  selector: 'app-course-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './course-edit.html',
})
export class CourseEdit implements OnInit {
  form!: FormGroup;
  course      = signal<CourseBase | null>(null);
  categories  = signal<CategoryBase[]>([]);
  teachers    = signal<TeacherDto[]>([]);
  loading     = signal(false);
  previewUrl  = signal<string | null>(null);
  imageError  = signal<string | null>(null);
  teacherMode = signal<TeacherFilter>('none');
  selectedTeacherId = signal<number | null>(null);

  private platformId    = inject(PLATFORM_ID);
  route                 = inject(ActivatedRoute);
  private router        = inject(Router);
  private fb            = inject(FormBuilder);
  private courseService = inject(CourseService);
  private categoryService = inject(CategoryService);
  private teacherService  = inject(TeacherService);
  public  ruleService     = inject(RuleService);

  courseCategory = computed(() =>
    this.categories().find(c => c.id === this.course()?.categoryId) ?? null
  );

  filteredTeachers = computed(() => {
    const mode = this.teacherMode();
    const all  = this.teachers();
    if (mode === 'by-category') {
      const catId = this.course()?.categoryId;
      return catId
        ? all.filter(t => t.specializations?.some(s => s.id === catId))
        : [];
    }
    if (mode === 'all') return all;
    return [];
  });

  ngOnInit(): void {
    this.form = this.fb.group({
      name:        ['', [Validators.required, Validators.minLength(3)]],
      description: ['', Validators.required],
      categoryId:  [null],
      photoUrl:    [null],
    });

    if (!isPlatformBrowser(this.platformId)) return;

    const id = Number(this.route.snapshot.paramMap.get('id'));

    forkJoin({
      course:     this.courseService.getById(id),
      categories: this.categoryService.getAll(),
      teachers:   this.teacherService.getAll(),           
    }).subscribe({
      next: ({ course, categories, teachers }) => {
        console.log('course:', course);    
        console.log('teachers:', teachers);   
        this.course.set(course);
        this.categories.set(categories);
        this.teachers.set(teachers);
        this.form.patchValue({
          name:        course.name,
          description: course.description,
          categoryId:  course.categoryId ?? null,
          photoUrl:    course.photoUrl ?? null,
        });
        this.previewUrl.set(course.photoUrl ?? null);
        this.selectedTeacherId.set(course.teacher ?? null);
        if (course.teacher) this.teacherMode.set('all');
      },
      error: err => console.error(err),
    });
  }

  setTeacherMode(mode: TeacherFilter): void {
    this.teacherMode.set(mode);
    if (mode === 'none') this.selectedTeacherId.set(null);
  }

  selectTeacher(id: number): void {
    this.selectedTeacherId.set(
      this.selectedTeacherId() === id ? null : id
    );
  }

  getTeacherById(id: number | null): TeacherDto | undefined {
    return id ? this.teachers().find(t => t.id === id) : undefined;
  }

  submit(): void {
    if (this.form.invalid) return;
    const id = this.course()?.id;
    if (!id) return;
    this.loading.set(true);

    const courseUpdate$ = this.courseService.update(id, this.form.value);
    const currentTeacherId = this.course()?.teacher ?? null;
    const newTeacherId = this.selectedTeacherId();

    const teacherChanged = currentTeacherId !== newTeacherId;

    if (!teacherChanged) {
      courseUpdate$.subscribe({
        next: () => this.router.navigate(['/courses', id]),
        error: () => this.loading.set(false),
      });
      return;
    }

    courseUpdate$.subscribe({
      next: () => {
        if (newTeacherId === null) {
          this.courseService.unassignTeacher(id).subscribe({
            next: () => this.router.navigate(['/courses', id]),
            error: () => this.loading.set(false),
          });
        } else {
          this.courseService.assignTeacher(id, newTeacherId).subscribe({
            next: () => this.router.navigate(['/courses', id]),
            error: () => this.loading.set(false),
          });
        }
      },
      error: () => this.loading.set(false),
    });
  }

  cancelLink(): string {
    return this.ruleService.isTeacher()
      ? '/profile/courses'
      : `/courses/${this.route.snapshot.paramMap.get('id')}`;
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
      this.form.patchValue({ photoUrl: result });
      this.previewUrl.set(result);
      this.imageError.set(null);
    };
    reader.onerror = () => {
      this.imageError.set('Не удалось прочитать изображение.');
    };
    reader.readAsDataURL(file);
  }

  clearPhoto(): void {
    this.form.patchValue({ photoUrl: '' });
    this.previewUrl.set(null);
    this.imageError.set(null);
  }
}
