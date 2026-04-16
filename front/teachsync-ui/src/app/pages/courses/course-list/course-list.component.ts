import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CourseService } from '../../../core/services/course.service';
import { CourseBase } from '../../../core/models/courses/course.model';

interface CourseGroup {
  category: string;
  courses: CourseBase[];
}

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './course-list.html',
})
export class CourseList implements OnInit {
  courses = signal<CourseBase[]>([]);

  grouped = computed<CourseGroup[]>(() => {
    const map = new Map<string, CourseBase[]>();
    for (const course of this.courses()) {
      const key = course.categoryName ?? 'Без категории';
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(course);
    }
    return Array.from(map.entries()).map(([category, courses]) => ({ category, courses }));
  });

  private platformId = inject(PLATFORM_ID);
  private courseService = inject(CourseService);

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.courseService.getAll().subscribe({
      next: data => this.courses.set(data),
      error: err => console.error(err),
    });
  }
}