import { Component, OnInit, signal, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CourseService } from '../../../../core/services/course.service';
import { CategoryService } from '../../../../core/services/category.service';
import { CategoryBase } from '../../../../core/models/category/category.model';
import { CourseDetailedMy, TopicTag } from '../../../../core/models/courses/course-detailed-my-model';

@Component({
  selector: 'app-my-courses',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-courses.component.html'
})
export class MyCoursesComponent implements OnInit {
  private courseService   = inject(CourseService);
  private categoryService = inject(CategoryService);

  courses    = signal<CourseDetailedMy[]>([]);
  categories = signal<CategoryBase[]>([]);
  loading    = signal(true);
  selected   = signal<CourseDetailedMy | null>(null);

  grouped = computed(() => {
    const map = new Map<string, CourseDetailedMy[]>();
    for (const c of this.courses()) {
      const key = c.categoryName ?? 'Без категории';
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(c);
    }
    return Array.from(map.entries()).map(([category, courses]) => ({ category, courses }));
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
        this.courses.set(d.map(c => ({
          ...c,
          id:     Number(c.id),
          topics: c.topics ?? [],
          groups: c.groups ?? []
        })));
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
    this.categoryService.getAll().subscribe(d => this.categories.set(d));
  }

  selectCourse(course: CourseDetailedMy): void {
    this.selected.set(course);
  }

  backToList(): void {
    this.selected.set(null);
  }

  getInitials(name: string): string {
    return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
  }

  getTagConfig(tag: TopicTag) {
    return this.tagConfig[tag] ?? { label: tag, classes: 'bg-slate-100 text-slate-600 border-slate-200' };
  }
}