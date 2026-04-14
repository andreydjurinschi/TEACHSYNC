import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CourseBase } from '../../../core/models/courses/course.model';
import { CourseService } from '../../../core/services/course.service';

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './course-list.html',
})
export class CourseList implements OnInit {
  courses = signal<CourseBase[]>([]);
  private platformId = inject(PLATFORM_ID);

  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.courseService.getAll().subscribe({
        next: data => this.courses.set(data),
        error: err => console.error(err)
      });
    }
  }
}
