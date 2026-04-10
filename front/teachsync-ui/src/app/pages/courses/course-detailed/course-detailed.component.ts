import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CourseBase } from '../../../core/models/courses/course.model';
import { CourseDetailed as CourseDetailedModel } from '../../../core/models/courses/course-detailed.model';
import { CourseWithTeacher } from '../../../core/models/courses/course-with-teacher.model';
import { CourseService } from '../../../core/services/course.service';

@Component({
  selector: 'app-course-detailed',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './course-detailed.html',
})
export class CourseDetailed implements OnInit {
  course = signal<CourseBase | null>(null);
  detailed = signal<CourseDetailedModel | null>(null);
  withTeacher = signal<CourseWithTeacher | null>(null);

  private platformId = inject(PLATFORM_ID);
  private route = inject(ActivatedRoute);

  constructor(private courseService: CourseService) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      const id = Number(this.route.snapshot.paramMap.get('id'));
      this.courseService.getById(id).subscribe({ next: data => this.course.set(data) });
      this.courseService.getDetailed(id).subscribe({ next: data => this.detailed.set(data) });
      this.courseService.getWithTeacher(id).subscribe({ next: data => this.withTeacher.set(data) });
    }
  }
}
