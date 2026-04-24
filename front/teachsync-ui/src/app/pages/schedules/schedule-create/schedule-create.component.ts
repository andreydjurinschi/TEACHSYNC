import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal, computed, OnDestroy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { Subscription, forkJoin } from 'rxjs';

import { ClassRoomInfo, GroupCourseInfo, WeekDay } from '../../../core/models/schedules/schedule-base.model';
import { ScheduleService } from '../../../core/services/schedule.service';

type LessonType = 'short' | 'full';

const DURATION: Record<LessonType, number> = { short: 45, full: 90 };
const DAY_ORDER: WeekDay[] = ['MON','TUE','WED','THU','FRI','SAT','SUN'];
const DAY_LABEL: Record<WeekDay, string> = {
  MON:'Пн', TUE:'Вт', WED:'Ср', THU:'Чт', FRI:'Пт', SAT:'Сб', SUN:'Вс'
};

function pad(n: number) { return String(n).padStart(2, '0'); }
function addMins(h: number, m: number, mins: number) {
  const t = h * 60 + m + mins;
  return { h: Math.floor(t / 60), m: t % 60 };
}
function timeStr(h: number, m: number) { return `${pad(h)}:${pad(m)}`; }

function buildSlots(): { h: number; m: number; label: string }[] {
  const slots: { h: number; m: number; label: string }[] = [];
  for (let h = 8; h <= 19; h++) {
    for (const m of [0, 15, 30, 45]) {
      if (h === 19 && m > 0) break;
      slots.push({ h, m, label: `${pad(h)}:${pad(m)}` });
    }
  }
  return slots;
}

export interface DaySlot {
  day: WeekDay;
  slotIndex: number | null; // индекс в validSlots
}

@Component({
  selector: 'app-schedule-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './schedule-create.html',
})
export class ScheduleCreateComponent implements OnInit, OnDestroy {
  private fb              = inject(FormBuilder);
  private scheduleService = inject(ScheduleService);
  private router          = inject(Router);
  private route           = inject(ActivatedRoute);
  private subs            = new Subscription();

  saving           = signal(false);
  error            = signal<string | null>(null);
  groupSize        = signal<number>(0);
  groupCourses     = signal<GroupCourseInfo[]>([]);
  classRooms       = signal<ClassRoomInfo[]>([]);
  busyIds          = signal<number[]>([]);
  teacherName      = signal<string | null>(null);
  lessonType       = signal<LessonType>('full');
  classRoomId      = signal<number | null>(null);
  groupCourseId    = signal<number | null>(null);

  // Каждый день — отдельная запись с выбранным слотом
  daySlots = signal<DaySlot[]>(
    DAY_ORDER.map(day => ({ day, slotIndex: null }))
  );

  // Активные (выбранные) дни
  activeDays = signal<WeekDay[]>([]);

  readonly ALL_DAYS = DAY_ORDER.map(k => ({ key: k, label: DAY_LABEL[k] }));

  readonly LESSON_TYPES: { key: LessonType; label: string; desc: string }[] = [
    { key: 'full',  label: 'Полная пара',  desc: '1 ч 30 мин' },
    { key: 'short', label: 'Сокращённая',  desc: '45 мин' },
  ];

  readonly allSlots = buildSlots();

  validSlots = computed(() => {
    const dur = DURATION[this.lessonType()];
    return this.allSlots.filter(s => s.h * 60 + s.m + dur <= 20 * 60);
  });

  // Для каждого активного дня — вычисляем endTime (или null)
  dayEndTimes = computed<Record<WeekDay, string | null>>(() => {
    const result = {} as Record<WeekDay, string | null>;
    for (const ds of this.daySlots()) {
      if (ds.slotIndex == null) { result[ds.day] = null; continue; }
      const slot = this.validSlots()[ds.slotIndex];
      if (!slot) { result[ds.day] = null; continue; }
      const end = addMins(slot.h, slot.m, DURATION[this.lessonType()]);
      result[ds.day] = (end.h > 20 || (end.h === 20 && end.m > 0))
        ? null
        : timeStr(end.h, end.m);
    }
    return result;
  });

  // Форма валидна если: выбрана группа, аудитория, минимум 1 день, и у всех активных дней есть время
  formValid = computed(() => {
    const active = this.activeDays();
    if (!this.groupCourseId() || !this.classRoomId() || active.length === 0) return false;
    const ends = this.dayEndTimes();
    return active.every(d => {
      const ds = this.daySlots().find(s => s.day === d);
      return ds?.slotIndex != null && ends[d] !== null;
    });
  });

  ngOnInit(): void {
    this.subs.add(
      this.scheduleService.getAllGroupCourses().subscribe(d => {
        this.groupCourses.set(d);
        this.subs.add(
          this.route.queryParams.subscribe(p => {
            if (p['groupCourseId']) this.selectGroupCourse(+p['groupCourseId']);
          })
        );
      })
    );
    this.subs.add(
      this.scheduleService.getAllClassrooms().subscribe(d => this.classRooms.set(d))
    );
  }

  ngOnDestroy(): void { this.subs.unsubscribe(); }

  selectGroupCourse(id: number): void {
    this.groupCourseId.set(id);
    const gc = this.groupCourses().find(g => g.id === id);
    this.teacherName.set(gc?.teacherName ?? null);
    this.subs.add(
      this.scheduleService.getGroupSize(id).subscribe(s => this.groupSize.set(s))
    );
  }

  setLessonType(t: LessonType): void {
    this.lessonType.set(t);
    // Сбрасываем слоты которые стали недопустимы
    this.daySlots.update(slots =>
      slots.map(s => {
        if (s.slotIndex == null) return s;
        const slot = this.validSlots()[s.slotIndex];
        if (!slot) return { ...s, slotIndex: null };
        return s;
      })
    );
    this.checkConflicts();
  }

  toggleDay(day: WeekDay): void {
    const active = this.activeDays();
    if (active.includes(day)) {
      this.activeDays.set(active.filter(d => d !== day));
      // Сбрасываем слот при снятии дня
      this.daySlots.update(slots =>
        slots.map(s => s.day === day ? { ...s, slotIndex: null } : s)
      );
    } else {
      this.activeDays.set([...active, day]);
    }
    this.checkConflicts();
  }

  isDayActive(day: WeekDay): boolean {
    return this.activeDays().includes(day);
  }

  setSlotForDay(day: WeekDay, idx: number): void {
    this.daySlots.update(slots =>
      slots.map(s => s.day === day ? { ...s, slotIndex: idx } : s)
    );
    this.checkConflicts();
  }

  getSlotForDay(day: WeekDay): number | null {
    return this.daySlots().find(s => s.day === day)?.slotIndex ?? null;
  }

  roomFits(r: ClassRoomInfo): boolean {
    return this.groupSize() === 0 || r.capacity >= this.groupSize();
  }

  selectRoom(id: number): void {
    if (!this.busyIds().includes(id)) this.classRoomId.set(id);
  }

  private checkConflicts(): void {
    const active = this.activeDays();
    const ends = this.dayEndTimes();
    const slots = this.daySlots();

    // Собираем все занятые аудитории для всех дней/времён
    const requests = active
      .map(day => {
        const ds = slots.find(s => s.day === day);
        if (ds?.slotIndex == null) return null;
        const slot = this.validSlots()[ds.slotIndex];
        const end = ends[day];
        if (!slot || !end) return null;
        return { day, start: timeStr(slot.h, slot.m), end };
      })
      .filter((x): x is { day: WeekDay; start: string; end: string } => x !== null);

    if (!requests.length) return;

    // Проверяем конфликты по всем дням сразу (объединяем результаты)
    this.subs.add(
      forkJoin(
        requests.map(r =>
          this.scheduleService.checkClassroomConflicts([r.day], r.start, r.end)
        )
      ).subscribe(results => {
        const allBusy = [...new Set(results.flat())];
        this.busyIds.set(allBusy);
        if (this.classRoomId() && allBusy.includes(this.classRoomId()!)) {
          this.classRoomId.set(null);
        }
      })
    );
  }

  submit(): void {
    if (!this.formValid()) return;
    this.saving.set(true);
    this.error.set(null);

    const slots = this.daySlots();
    const ends = this.dayEndTimes();
    const validSlots = this.validSlots();

    // Создаём отдельную запись на каждый активный день
    const requests = this.activeDays().map(day => {
      const ds = slots.find(s => s.day === day)!;
      const slot = validSlots[ds.slotIndex!];
      const end = addMins(slot.h, slot.m, DURATION[this.lessonType()]);
      return this.scheduleService.create({
        startTime:     [slot.h, slot.m],
        endTime:       [end.h, end.m],
        weekDays:      [day],
        groupCourseId: this.groupCourseId(),
        classRoomId:   this.classRoomId(),
      });
    });

    this.subs.add(
      forkJoin(requests).subscribe({
        next:  () => this.router.navigate(['/schedules']),
        error: err => {
          this.error.set(err?.error?.message ?? 'Ошибка при создании');
          this.saving.set(false);
        },
      })
    );
  }
}