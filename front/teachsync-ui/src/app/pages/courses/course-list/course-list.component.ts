import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, computed, effect, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CourseService } from '../../../core/services/course.service';
import { CourseBase } from '../../../core/models/courses/course.model';
import { RuleService } from '../../../core/services/role.rule.service';
import { PaginationControlsComponent } from '../../../shared/pagination/pagination-controls.component';
import { getTotalPages, paginateItems } from '../../../shared/pagination/pagination.utils';

interface CourseGroup {
  category: string;
  courses: CourseBase[];
}

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [CommonModule, RouterLink, PaginationControlsComponent],
  templateUrl: './course-list.html',
})
export class CourseList implements OnInit {
  courses = signal<CourseBase[]>([]);
  currentPage = signal(1);
  private readonly pageSize = 9;
  totalPages = computed(() => getTotalPages(this.courses().length, this.pageSize));
  pagedCourses = computed(() => paginateItems(this.courses(), this.currentPage(), this.pageSize));
  
  grouped = computed<CourseGroup[]>(() => {
    const map = new Map<string, CourseBase[]>();
    for (const course of this.pagedCourses()) {
      const key = course.categoryName ?? 'Без категории';
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(course);
    }
    return Array.from(map.entries()).map(([category, courses]) => ({ category, courses }));
  });
  
  private platformId = inject(PLATFORM_ID);
  private courseService = inject(CourseService);
  public ruleService = inject(RuleService);

  constructor() {
    effect(() => {
      const maxPage = this.totalPages();
      if (this.currentPage() > maxPage) {
        this.currentPage.set(maxPage);
      }
    });
  }

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.courseService.getAll().subscribe({
      next: data => {
        this.courses.set(data);
        this.currentPage.set(1);
      },
      error: err => console.error(err),
    });
  }
}
