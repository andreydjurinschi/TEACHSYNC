import { CommonModule } from '@angular/common';
import {
  Component,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';

import {
  ClassRoomInfo,
  GroupCourseInfo,
  WeekDay,
} from '../../../core/models/schedules/schedule-base.model';
import { ScheduleService } from '../../../core/services/schedule.service';

@Component({
  selector: 'app-schedule-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './schedule-create.html',
})
export class ScheduleCreate implements OnInit {
  form!: FormGroup;
  saving = signal(false);
  error = signal<string | null>(null);
  groupSize = signal<number>(0);

  groupCourses = signal<GroupCourseInfo[]>([]);
  classRooms = signal<ClassRoomInfo[]>([]);

  private fb = inject(FormBuilder);
  private scheduleService = inject(ScheduleService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);


  selectedTeacherName = signal<string | null>(null);

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

busyClassroomIds = signal<number[]>([]);

private checkConflicts(): void {
    const v = this.form.value;
    const days: string[] = v.weekDays ?? [];
    const startTime = v.startHour != null && v.startMinute != null
        ? `${String(v.startHour).padStart(2,'0')}:${String(v.startMinute).padStart(2,'0')}`
        : null;
    const endTime = v.endHour != null && v.endMinute != null
        ? `${String(v.endHour).padStart(2,'0')}:${String(v.endMinute).padStart(2,'0')}`
        : null;

    if (days.length && startTime && endTime) {
        this.scheduleService.checkClassroomConflicts(days, startTime, endTime)
            .subscribe(ids => this.busyClassroomIds.set(ids));
    }
}

ngOnInit(): void {
    this.form = this.fb.group({
        startHour:     [null, Validators.required],
        startMinute:   [null, Validators.required],
        endHour:       [null, Validators.required],
        endMinute:     [null, Validators.required],
        weekDays:      [[], Validators.required],
        groupCourseId: [null, Validators.required],
        classRoomId:   [null, Validators.required],
    });

  this.scheduleService.getAllGroupCourses().subscribe(d => {
    this.groupCourses.set(d);

    this.route.queryParams.subscribe(params => {
      if (params['groupCourseId']) {
        const id = +params['groupCourseId'];
        this.form.get('groupCourseId')!.setValue(id);
      }
    });
  });
    this.scheduleService.getAllClassrooms().subscribe(d => this.classRooms.set(d));

    this.form.get('groupCourseId')!.valueChanges.subscribe(id => {
        const gc = this.groupCourses().find(g => g.id === +id);
        this.selectedTeacherName.set(gc?.teacherName ?? null);
        this.scheduleService.getGroupSize(+id).subscribe(size => {
            this.groupSize.set(size);
        });
    });

    ['startHour', 'startMinute', 'endHour', 'endMinute', 'weekDays'].forEach(field =>
        this.form.get(field)!.valueChanges.subscribe(() => this.checkConflicts())
    );
}

  roomFits(room: ClassRoomInfo): boolean {
  if (this.groupSize() === 0) return true; 
  return room.capacity >= this.groupSize();
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
    weekDays:  v.weekDays, 
    groupCourseId: v.groupCourseId,
    classRoomId:   v.classRoomId,
  };

  this.saving.set(true);
  this.scheduleService.create(payload).subscribe({
    next: () => { this.saving.set(false); this.router.navigate(['/schedules']); },
    error: err => {
      console.error(err);
      const msg = err?.error?.message ?? err?.error ?? 'Ошибка при создании';
      this.error.set(msg);
      this.saving.set(false);
    }
  });
}
}