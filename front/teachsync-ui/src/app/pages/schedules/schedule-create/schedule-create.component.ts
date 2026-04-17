import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ScheduleService } from '../../../core/services/schedule.service';
import { ClassRoomInfo, GroupCourseInfo, TeacherInfo, WeekDay } from '../../../core/models/schedules/schedule-base.model';

@Component({
  selector: 'app-schedule-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './schedule-create.html',
})
export class ScheduleCreate implements OnInit {
  form!: FormGroup;
  saving = signal(false);
  error = signal<string | null>(null);

  teachers = signal<TeacherInfo[]>([]);
  groupCourses = signal<GroupCourseInfo[]>([]);
  classRooms = signal<ClassRoomInfo[]>([]);

  private fb = inject(FormBuilder);
  private scheduleService = inject(ScheduleService);
  private router = inject(Router);

  readonly ALL_DAYS: { key: WeekDay; label: string }[] = [
    { key: 'MON', label: 'Пн' },
    { key: 'TUE', label: 'Вт' },
    { key: 'WED', label: 'Ср' },
    { key: 'THU', label: 'Чт' },
    { key: 'FRI', label: 'Пт' },
    { key: 'SAT', label: 'Сб' },
    { key: 'SUN', label: 'Вс' },
  ];

readonly hours   = Array.from({length: 24}, (_, i) => i);  
readonly minutes = [0, 15, 30, 45];          

  ngOnInit(): void {
this.form = this.fb.group({
  startHour:    [null, Validators.required],
  startMinute:  [null, Validators.required],
  endHour:      [null, Validators.required],
  endMinute:    [null, Validators.required],
  weekDays:     [[], Validators.required],
  groupCourseId:[null, Validators.required],
  teacherId:    [null, Validators.required],
  classRoomId:  [null, Validators.required],
});

    this.scheduleService.getAllTeachers().subscribe(d => this.teachers.set(d));
    this.scheduleService.getAllGroupCourses().subscribe(d => this.groupCourses.set(d));
    this.scheduleService.getAllClassrooms().subscribe(d => this.classRooms.set(d));
  }

  toggleDay(day: WeekDay): void {
    const current: WeekDay[] = this.form.get('weekDays')!.value;
    const updated = current.includes(day)
      ? current.filter(d => d !== day)
      : [...current, day];
    this.form.get('weekDays')!.setValue(updated);
  }

  isDaySelected(day: WeekDay): boolean {
    return (this.form.get('weekDays')!.value as WeekDay[]).includes(day);
  }

submit(): void {
  if (this.form.invalid) return;
  const v = this.form.value;

  const payload = {
    startTime: `${String(v.startHour).padStart(2,'0')}:${String(v.startMinute).padStart(2,'0')}`,
    endTime:   `${String(v.endHour).padStart(2,'0')}:${String(v.endMinute).padStart(2,'0')}`,
    weekDays:      v.weekDays,
    groupCourseId: v.groupCourseId,
    teacherId:     v.teacherId,
    classRoomId:   v.classRoomId,
  };

  this.saving.set(true);
  this.scheduleService.create(payload).subscribe({
    next: () => { this.saving.set(false); this.router.navigate(['/schedules']); },
    error: err => { console.error(err); this.error.set('Ошибка при создании'); this.saving.set(false); }
  });
}
}