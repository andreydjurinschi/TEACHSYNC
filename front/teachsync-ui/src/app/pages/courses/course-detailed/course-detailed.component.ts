import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, computed, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
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
  currentTeacherSpecializationIds = signal<number[]>([]);
  assignmentRequestMode = signal(false);
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

  canApproveRequest = computed(() => {
    const course = this.course();
    if (!course || !this.assignmentRequestMode() || !this.ruleSevice.isTeacher() || course.teacher != null) {
      return false;
    }
    if (course.categoryId == null) {
      return true;
    }
    return this.currentTeacherSpecializationIds().includes(course.categoryId);
  });

  private platformId = inject(PLATFORM_ID);
  private route = inject(ActivatedRoute);
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
      this.route.queryParamMap.subscribe(params => {
        this.assignmentRequestMode.set(params.get('assignmentRequest') === 'true');
      });
      if (this.ruleSevice.isManager() || this.ruleSevice.isAdmin()) {
        this.teacherService.getAll().subscribe({
          next: data => this.teachers.set(data),
          error: () => this.teachers.set([])
        });
      }
      if (this.ruleSevice.isTeacher()) {
        const teacherId = this.ruleSevice.getId();
        if (teacherId != null) {
          this.teacherService.getSpecializations(teacherId).subscribe({
            next: specs => this.currentTeacherSpecializationIds.set(specs.map(spec => spec.id)),
            error: () => this.currentTeacherSpecializationIds.set([])
          });
        }
      }
    }
  }

  onTeacherSelect(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedTeacherId.set(value ? Number(value) : null);
  }

  requestSelectedTeacher(): void {
    const courseId = this.course()?.id;
    const teacherId = this.selectedTeacherId();
    if (courseId == null || teacherId == null) return;
    this.pending.set(true);
    this.actionMessage.set(null);
    this.actionError.set(null);
    this.courseService.requestTeacher(courseId, teacherId).subscribe({
      next: () => {
        this.pending.set(false);
        this.actionMessage.set('Запрос отправлен преподавателю.');
      },
      error: err => {
        this.pending.set(false);
        this.actionError.set(err?.error?.message ?? 'Не удалось отправить запрос.');
      }
    });
  }

  approveRequest(): void {
    const courseId = this.course()?.id;
    const teacherId = this.ruleSevice.getId();
    if (courseId == null || teacherId == null || !this.canApproveRequest()) return;
    this.pending.set(true);
    this.actionMessage.set(null);
    this.actionError.set(null);
    this.courseService.approveTeacherRequest(courseId, teacherId).subscribe({
      next: () => {
        this.pending.set(false);
        this.actionMessage.set('Запрос подтвержден. Курс назначен вам.');
        this.course.update(course => course ? { ...course, teacher: teacherId } : course);
        this.assignmentRequestMode.set(false);
      },
      error: err => {
        this.pending.set(false);
        this.actionError.set(err?.error?.message ?? 'Не удалось подтвердить запрос.');
      }
    });
  }
}
