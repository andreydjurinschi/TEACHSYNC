import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, computed, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CourseBase } from '../../../core/models/courses/course.model';
import { CourseDetailed as CourseDetailedModel } from '../../../core/models/courses/course-detailed.model';
import { CourseWithTeacher } from '../../../core/models/courses/course-with-teacher.model';
import { TeacherDto } from '../../../core/models/users/teacher.model';
import { CourseService } from '../../../core/services/course.service';
import { RuleService } from '../../../core/services/role.rule.service';
import { TeacherService } from '../../../core/services/teacher.service';

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
  teachers = signal<TeacherDto[]>([]);
  selectedTeacherId = signal<number | null>(null);
  pending = signal(false);
  actionMessage = signal<string | null>(null);
  actionError = signal<string | null>(null);

  availableTeachers = computed(() => {
    const course = this.course();
    const categoryId = course?.categoryId;
    return this.teachers().filter(teacher =>
      categoryId == null || teacher.specializations?.some(spec => spec.id === categoryId)
    );
  });

  canManageCourse = computed(() => {
    if (this.ruleSevice.isManager() || this.ruleSevice.isAdmin()) return true;
    if (!this.ruleSevice.isTeacher()) return false;
    const teacherId = this.ruleSevice.getId();
    if (teacherId == null) return false;
    return this.course()?.teacher === teacherId;
  });

  canManageGroups = computed(() =>
    this.ruleSevice.isManager() || this.ruleSevice.isAdmin()
  );

  canDeleteCourse = computed(() =>
    this.ruleSevice.isManager() || this.ruleSevice.isAdmin()
  );

  private platformId = inject(PLATFORM_ID);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  public ruleSevice = inject(RuleService)

  constructor(private courseService: CourseService, private teacherService: TeacherService) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      const id = Number(this.route.snapshot.paramMap.get('id'));
      this.courseService.getById(id).subscribe({ next: data => this.course.set(data) });
      this.courseService.getDetailed(id).subscribe({ next: data => this.detailed.set(data) });
      this.courseService.getWithTeacher(id).subscribe({
        next: data => this.withTeacher.set(data),
        error: () => this.withTeacher.set(null)
      });
      if (this.ruleSevice.isManager() || this.ruleSevice.isAdmin()) {
        this.teacherService.getAll().subscribe({
          next: data => this.teachers.set(data),
          error: () => this.teachers.set([])
        });
      }
    }
  }

  onTeacherSelect(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedTeacherId.set(value ? Number(value) : null);
  }

  assignSelectedTeacher(): void {
    const courseId = this.course()?.id;
    const teacherId = this.selectedTeacherId();
    if (courseId == null || teacherId == null) return;
    this.pending.set(true);
    this.actionMessage.set(null);
    this.actionError.set(null);
    this.courseService.assignTeacher(courseId, teacherId).subscribe({
      next: () => {
        this.course.update(course => course ? { ...course, teacher: teacherId } : course);
        this.courseService.getWithTeacher(courseId).subscribe({
          next: data => this.withTeacher.set(data),
          error: () => this.withTeacher.set(null)
        });
        this.pending.set(false);
        this.actionMessage.set('Преподаватель назначен на курс.');
      },
      error: err => {
        this.pending.set(false);
        this.actionError.set(err?.error?.message ?? 'Не удалось назначить преподавателя.');
      }
    });
  }

  deleteCourse(): void {
    const courseId = this.course()?.id;
    if (courseId == null) return;
    if (!confirm('Удалить этот курс? Связанные расписания и заявки на замену будут очищены автоматически.')) {
      return;
    }
    this.pending.set(true);
    this.actionMessage.set(null);
    this.actionError.set(null);
    this.courseService.delete(courseId).subscribe({
      next: () => {
        this.pending.set(false);
        this.router.navigate(['/courses']);
      },
      error: err => {
        this.pending.set(false);
        this.actionError.set(err?.error?.message ?? 'Не удалось удалить курс.');
      }
    });
  }

  backLink(): string {
    return '/courses';
  }
}
