import { Component, OnInit, signal, inject, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CourseService } from '../../../../core/services/course.service';
import { CourseDetailedMy, TopicTag } from '../../../../core/models/courses/course-detailed-my-model';
import { PaginationControlsComponent } from '../../../../shared/pagination/pagination-controls.component';
import { getTotalPages, paginateItems } from '../../../../shared/pagination/pagination.utils';

@Component({
  selector: 'app-my-courses',
  standalone: true,
  imports: [CommonModule, RouterLink, PaginationControlsComponent],
  templateUrl: './my-courses.component.html'
})
export class MyCoursesComponent implements OnInit {
  private courseService   = inject(CourseService);

  courses    = signal<CourseDetailedMy[]>([]);
  loading    = signal(true);
  selected   = signal<CourseDetailedMy | null>(null);
  currentPage = signal(1);
  private readonly pageSize = 6;
  totalPages = computed(() => getTotalPages(this.courses().length, this.pageSize));
  pagedCourses = computed(() => paginateItems(this.courses(), this.currentPage(), this.pageSize));

  grouped = computed(() => {
    const map = new Map<string, CourseDetailedMy[]>();
    for (const c of this.pagedCourses()) {
      const key = c.categoryName ?? 'Без категории';
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(c);
    }
    return Array.from(map.entries()).map(([category, courses]) => ({ category, courses }));
  });

  readonly tagConfig: Record<TopicTag, { label: string; classes: string }> = {
    IT:       { label: 'IT',          classes: 'bg-blue-50 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-blue-200 dark:border-blue-800' },
    DESIGN:   { label: 'Дизайн',     classes: 'bg-blue-50 text-blue-800 dark:bg-blue-950/40 dark:text-blue-300 border-blue-200 dark:border-blue-900/70' },
    MATH:     { label: 'Математика', classes: 'bg-emerald-50 text-emerald-800 dark:bg-emerald-950/40 dark:text-emerald-300 border-emerald-200 dark:border-emerald-900/70' },
    LANGUAGE: { label: 'Языки',      classes: 'bg-emerald-50 text-emerald-800 dark:bg-emerald-950/40 dark:text-emerald-300 border-emerald-200 dark:border-emerald-900/70' },
    BUSINESS: { label: 'Бизнес',     classes: 'bg-slate-50 text-slate-700 dark:bg-slate-800 dark:text-slate-300 border-slate-200 dark:border-slate-700' },
    SCIENCE:  { label: 'Наука',      classes: 'bg-emerald-50 text-emerald-800 dark:bg-emerald-950/40 dark:text-emerald-300 border-emerald-200 dark:border-emerald-900/70' },
  };

  constructor() {
    effect(() => {
      const maxPage = this.totalPages();
      if (this.currentPage() > maxPage) {
        this.currentPage.set(maxPage);
      }
    });
  }

  ngOnInit(): void {
    this.courseService.getMyCoursesDetailed().subscribe({
      next: d => {
        this.courses.set(d.map(c => ({
          ...c,
          id:     Number(c.id),
          topics: c.topics ?? [],
          groups: c.groups ?? []
        })));
        this.currentPage.set(1);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
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
    const labels: Record<TopicTag, string> = {
      IT: 'IT',
      DESIGN: 'Design',
      MATH: 'Math',
      LANGUAGE: 'Language',
      BUSINESS: 'Business',
      SCIENCE: 'Science',
    };
    const classes: Record<TopicTag, string> = {
      IT: 'bg-blue-50 text-blue-700 dark:bg-blue-950/40 dark:text-blue-300 border-blue-200 dark:border-blue-900',
      DESIGN: 'bg-slate-50 text-slate-700 dark:bg-slate-900 dark:text-slate-300 border-slate-200 dark:border-slate-700',
      MATH: 'bg-blue-50 text-blue-700 dark:bg-blue-950/40 dark:text-blue-300 border-blue-200 dark:border-blue-900',
      LANGUAGE: 'bg-emerald-50 text-emerald-700 dark:bg-emerald-950/40 dark:text-emerald-300 border-emerald-200 dark:border-emerald-900',
      BUSINESS: 'bg-slate-50 text-slate-700 dark:bg-slate-900 dark:text-slate-300 border-slate-200 dark:border-slate-700',
      SCIENCE: 'bg-emerald-50 text-emerald-700 dark:bg-emerald-950/40 dark:text-emerald-300 border-emerald-200 dark:border-emerald-900',
    };
    return {
      label: labels[tag] ?? tag,
      classes: classes[tag] ?? 'bg-slate-50 text-slate-700 border-slate-200'
    };
  }
}
