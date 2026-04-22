import { Component, OnInit, signal, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CourseService } from '../../../../core/services/course.service';
import { CategoryService } from '../../../../core/services/category.service';
import { CourseBase } from '../../../../core/models/courses/course.model';
import { CategoryBase } from '../../../../core/models/category/category.model';
import { CourseDetailedMy, TopicTag } from '../../../../core/models/courses/course-detailed-my-model';

interface CourseGroup {
  category: string;
  courses: CourseBase[];
}

@Component({
  selector: 'app-my-courses',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './my-courses.component.html'
})
export class MyCoursesComponent implements OnInit {
  private courseService = inject(CourseService);
  private categoryService = inject(CategoryService);
  private fb = inject(FormBuilder);

  courses    = signal<CourseDetailedMy[]>([]);
  categories = signal<CategoryBase[]>([]);
  loading    = signal(true);
  saving     = signal(false);
  error      = signal<string | null>(null);

  selected  = signal<CourseDetailedMy | null>(null);
  editingId = signal<number | null>(null);

  grouped = computed(() => {
    const map = new Map<string, CourseDetailedMy[]>();
    for (const c of this.courses()) {
      const key = c.categoryName ?? 'Без категории';
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(c);
    }
    return Array.from(map.entries()).map(([category, courses]) => ({ category, courses }));
  });

  editForm = this.fb.nonNullable.group({
    name:        ['', Validators.required],
    description: [''],
    photoUrl:    [''],
    categoryId:  [0]
  });

  readonly tagConfig: Record<TopicTag, { label: string; classes: string }> = {
    IT:       { label: 'IT',          classes: 'bg-blue-50 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-blue-200 dark:border-blue-800' },
    DESIGN:   { label: 'Дизайн',     classes: 'bg-purple-50 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400 border-purple-200 dark:border-purple-800' },
    MATH:     { label: 'Математика', classes: 'bg-amber-50 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400 border-amber-200 dark:border-amber-800' },
    LANGUAGE: { label: 'Языки',      classes: 'bg-green-50 text-green-700 dark:bg-green-900/30 dark:text-green-400 border-green-200 dark:border-green-800' },
    BUSINESS: { label: 'Бизнес',     classes: 'bg-orange-50 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400 border-orange-200 dark:border-orange-800' },
    SCIENCE:  { label: 'Наука',      classes: 'bg-teal-50 text-teal-700 dark:bg-teal-900/30 dark:text-teal-400 border-teal-200 dark:border-teal-800' },
  };

ngOnInit(): void {
  this.courseService.getMyCoursesDetailed().subscribe({
    next: d => {
      const normalized = d.map(c => ({
        ...c,
        topics: c.topics ?? [],
        groups: c.groups ?? []
      }));
      this.courses.set(normalized);
      this.loading.set(false);
    },
    error: () => this.loading.set(false)
  });
  this.categoryService.getAll().subscribe(d => this.categories.set(d));
}

  selectCourse(course: CourseDetailedMy): void {
    this.selected.set(course);   // ← данные уже есть, никакого доп. запроса
    this.editingId.set(null);
    this.error.set(null);
  }

  backToList(): void {
    this.selected.set(null);
    this.editingId.set(null);
    this.error.set(null);
  }

  startEdit(course: CourseDetailedMy): void {
    this.editingId.set(course.id);
    this.error.set(null);
    this.editForm.patchValue({
      name:        course.name,
      description: course.description ?? '',
      photoUrl:    course.photoUrl    ?? '',
      categoryId:  course.categoryId  ?? 0
    });
  }

  cancelEdit(): void {
    this.editingId.set(null);
    this.error.set(null);
  }

  saveEdit(courseId: number): void {
    if (this.editForm.invalid) return;
    this.saving.set(true);
    const v = this.editForm.value;
    const payload: Partial<CourseBase> = {
      name:        v.name        || undefined,
      description: v.description || undefined,
      photoUrl:    v.photoUrl    || undefined,
      categoryId:  v.categoryId  || undefined,
    };
    this.courseService.update(courseId, payload).subscribe({
      next: () => {
        const catName = this.categories().find(c => c.id === payload.categoryId)?.name;
        const updated: CourseDetailedMy = {
          ...this.courses().find(c => c.id === courseId)!,
          ...payload,
          categoryName: catName
        };
        this.courses.update(list => list.map(c => c.id === courseId ? updated : c));
        this.selected.set(updated);
        this.editingId.set(null);
        this.saving.set(false);
      },
      error: err => {
        this.error.set(err?.error?.message ?? 'Ошибка при сохранении');
        this.saving.set(false);
      }
    });
  }

  getInitials(name: string): string {
    return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
  }

  getTagConfig(tag: TopicTag) {
    return this.tagConfig[tag] ?? { label: tag, classes: 'bg-slate-100 text-slate-600 border-slate-200' };
  }
}