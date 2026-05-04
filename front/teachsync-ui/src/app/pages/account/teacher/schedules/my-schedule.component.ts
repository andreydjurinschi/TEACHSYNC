import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ScheduleService } from '../../../../core/services/schedule.service';
import { ScheduleBase, WeekDay } from '../../../../core/models/schedules/schedule-base.model';
import { ReplacementRequest } from '../../../../core/models/replacements/replacement.model';
import { ReplacementService } from '../../../../core/services/replacement.service';
import { RuleService } from '../../../../core/services/role.rule.service';

const DAY_ORDER: WeekDay[] = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];
const DAY_LABELS: Record<WeekDay, string> = {
  MON: 'Пн',
  TUE: 'Вт',
  WED: 'Ср',
  THU: 'Чт',
  FRI: 'Пт',
  SAT: 'Сб',
  SUN: 'Вс'
};
const MONTH_LABELS = [
  'Январь',
  'Февраль',
  'Март',
  'Апрель',
  'Май',
  'Июнь',
  'Июль',
  'Август',
  'Сентябрь',
  'Октябрь',
  'Ноябрь',
  'Декабрь'
];

interface CalendarDay {
  date: Date;
  dateKey: string;
  dayNumber: number;
  inCurrentMonth: boolean;
  isToday: boolean;
  lessonsCount: number;
  temporaryLessonsCount: number;
  hasPastLessonsOnly: boolean;
}

@Component({
  selector: 'app-teacher-schedule',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './my-schedule.component.html'
})
export class TeacherScheduleComponent implements OnInit {
  private scheduleService = inject(ScheduleService);
  private replacementService = inject(ReplacementService);
  private ruleService = inject(RuleService);
  private route = inject(ActivatedRoute);

  schedules = signal<ScheduleBase[]>([]);
  replacements = signal<ReplacementRequest[]>([]);
  loading = signal(true);
  monthCursor = signal(this.startOfMonth(new Date()));
  selectedDate = signal(this.toDateKey(new Date()));
  selectedScheduleId = signal<number | null>(null);
  replacementReason = signal('Нужна замена на эту пару.');
  actionMessage = signal<string | null>(null);
  actionError = signal<string | null>(null);
  submittingScheduleId = signal<number | null>(null);

  readonly DAY_ORDER = DAY_ORDER;
  readonly DAY_LABELS = DAY_LABELS;

  readonly monthTitle = computed(() => {
    const cursor = this.monthCursor();
    return `${MONTH_LABELS[cursor.getMonth()]} ${cursor.getFullYear()}`;
  });

  readonly calendarDays = computed(() => this.buildCalendarDays(this.monthCursor()));
  readonly selectedLessons = computed(() => this.lessonsForDate(this.selectedDate()));
  readonly selectedTemporaryReplacements = computed(() => this.temporaryReplacementsForDate(this.selectedDate()));
  readonly selectedDateLabel = computed(() => this.formatDateLabel(this.selectedDate()));

  ngOnInit(): void {
    this.scheduleService.getMySchedule().subscribe({
      next: data => {
        this.schedules.set(data);
        this.loading.set(false);
        this.selectInitialDay(data);
      },
      error: () => {
        this.loading.set(false);
        this.actionError.set('Не удалось загрузить расписание преподавателя.');
      }
    });

    this.loadReplacements();
    this.route.queryParamMap.subscribe(params => {
      const replacementRequestId = Number(params.get('replacementRequestId'));
      if (replacementRequestId) {
        this.approveReplacement(replacementRequestId);
      }
    });
  }

  loadReplacements(): void {
    const teacherId = this.ruleService.getId();
    if (teacherId == null) {
      return;
    }
    this.replacementService.getForTeacher(teacherId).subscribe({
      next: data => this.replacements.set(data),
      error: () => this.replacements.set([])
    });
  }

  previousMonth(): void {
    const current = this.monthCursor();
    this.monthCursor.set(new Date(current.getFullYear(), current.getMonth() - 1, 1));
  }

  nextMonth(): void {
    const current = this.monthCursor();
    this.monthCursor.set(new Date(current.getFullYear(), current.getMonth() + 1, 1));
  }

  goToToday(): void {
    const today = new Date();
    this.monthCursor.set(this.startOfMonth(today));
    this.selectDate(this.toDateKey(today));
  }

  selectDate(dateKey: string): void {
    this.selectedDate.set(dateKey);
    this.selectedScheduleId.set(null);
    this.actionMessage.set(null);
    this.actionError.set(null);
  }

  selectLesson(schedule: ScheduleBase): void {
    this.selectedScheduleId.set(schedule.id);
  }

  requestReplacement(schedule: ScheduleBase): void {
    const teacherId = this.ruleService.getId();
    if (teacherId == null) {
      this.actionError.set('Не удалось определить преподавателя.');
      return;
    }

    const lessonDate = this.selectedDate();
    if (!this.isLessonAvailableForReplacement(schedule, lessonDate)) {
      this.actionError.set('Заявку можно отправить только на будущую пару из выбранного дня.');
      return;
    }

    const reason = this.replacementReason().trim() || 'Нужна замена на эту пару.';
    this.actionMessage.set(null);
    this.actionError.set(null);
    this.submittingScheduleId.set(schedule.id);

    this.replacementService.create({
      scheduleId: schedule.id,
      teacherRequested: teacherId,
      lessonDate,
      reason
    }).subscribe({
      next: request => {
        this.actionMessage.set(`Запрос на замену создан на ${this.formatDateLabel(lessonDate)}.`);
        this.replacements.update(list => [request, ...list]);
        this.submittingScheduleId.set(null);
      },
      error: err => {
        this.actionError.set(err?.error?.message ?? 'Не удалось создать запрос на замену.');
        this.submittingScheduleId.set(null);
      }
    });
  }

  approveReplacement(requestId: number): void {
    const teacherId = this.ruleService.getId();
    if (teacherId == null) {
      return;
    }
    this.actionMessage.set(null);
    this.actionError.set(null);
    this.replacementService.approve(requestId, teacherId).subscribe({
      next: request => {
        this.actionMessage.set('Вы подтвердили замену. Преподавателю отправлено уведомление.');
        this.replacements.update(list => {
          const rest = list.filter(item => item.id !== request.id);
          return [request, ...rest];
        });
      },
      error: err => this.actionError.set(err?.error?.message ?? 'Не удалось подтвердить замену.')
    });
  }

  declineReplacement(requestId: number): void {
    const teacherId = this.ruleService.getId();
    if (teacherId == null) {
      return;
    }
    this.actionMessage.set(null);
    this.actionError.set(null);
    this.replacementService.decline(requestId, teacherId).subscribe({
      next: request => {
        this.actionMessage.set('Вы отказались от временной пары.');
        this.replacements.update(list => {
          const rest = list.filter(item => item.id !== request.id);
          return [request, ...rest];
        });
      },
      error: err => this.actionError.set(err?.error?.message ?? 'Не удалось отказаться от замены.')
    });
  }

  lessonsForDate(dateKey: string): ScheduleBase[] {
    const weekday = this.weekdayFromDateKey(dateKey);
    return this.schedules()
      .filter(schedule => schedule.weekDays.includes(weekday))
      .sort((a, b) => this.minutes(a.startTime) - this.minutes(b.startTime));
  }

  replacementFor(schedule: ScheduleBase, dateKey: string): ReplacementRequest | undefined {
    return this.replacements().find(item =>
      item.scheduleBaseDtoRequest?.id === schedule.id && this.normalizeDateKey(item.lessonDate) === dateKey
    );
  }

  temporaryReplacementsForDate(dateKey: string): ReplacementRequest[] {
    return this.replacements()
      .filter(item => this.isTemporaryReplacementForCurrentTeacher(item))
      .filter(item => this.normalizeDateKey(item.lessonDate) === dateKey)
      .sort((a, b) =>
        this.minutes(a.scheduleBaseDtoRequest?.startTime) - this.minutes(b.scheduleBaseDtoRequest?.startTime)
      );
  }

  isTemporaryReplacementForCurrentTeacher(replacement: ReplacementRequest): boolean {
    const teacherId = this.ruleService.getId();
    if (teacherId == null || replacement.teacherBaseInfoRequest?.id === teacherId) {
      return false;
    }

    if (replacement.status === 'PENDING') {
      return true;
    }

    return replacement.status === 'APPROVED'
      && replacement.approvedByTeacherBaseInfoRequest?.id === teacherId;
  }

  temporaryReplacementLabel(replacement: ReplacementRequest): string {
    return replacement.status === 'APPROVED' ? 'Временная · подтверждена' : 'Временная · можно заменить';
  }

  canRespondToTemporaryReplacement(replacement: ReplacementRequest): boolean {
    const lessonDate = this.normalizeDateKey(replacement.lessonDate);
    if (!lessonDate || replacement.status !== 'PENDING') {
      return false;
    }
    return this.lessonEndDate(replacement.scheduleBaseDtoRequest, lessonDate) > new Date();
  }

  canRequestReplacement(schedule: ScheduleBase, dateKey: string): boolean {
    const replacement = this.replacementFor(schedule, dateKey);
    return this.isLessonAvailableForReplacement(schedule, dateKey)
      && (!replacement || ['CANCELLED', 'EXPIRED', 'AUTO_CLOSED'].includes(replacement.status));
  }

  isLessonAvailableForReplacement(schedule: ScheduleBase, dateKey: string): boolean {
    if (!schedule.weekDays.includes(this.weekdayFromDateKey(dateKey))) {
      return false;
    }
    return this.lessonEndDate(schedule, dateKey) > new Date();
  }

  replacementSummary(): ReplacementRequest[] {
    const teacherId = this.ruleService.getId();
    return this.replacements().filter(item =>
      item.teacherBaseInfoRequest?.id === teacherId || item.approvedByTeacherBaseInfoRequest?.id === teacherId
    );
  }

  teacherName(teacher?: { name?: string; surname?: string; fullName?: string }): string {
    if (!teacher) {
      return '—';
    }
    return teacher.fullName || `${teacher.name ?? ''} ${teacher.surname ?? ''}`.trim();
  }

  replacementStatusLabel(status: ReplacementRequest['status']): string {
    const map: Record<ReplacementRequest['status'], string> = {
      PENDING: 'Поиск',
      APPROVED: 'Подтверждена',
      DECLINED: 'Отклонена',
      EXPIRED: 'Просрочена',
      CANCELLED: 'Отменена',
      AUTO_CLOSED: 'Закрыта'
    };
    return map[status];
  }

  formatTime(time: number[] | undefined): string {
    if (!time || time.length < 2) {
      return '—';
    }
    return `${String(time[0]).padStart(2, '0')}:${String(time[1]).padStart(2, '0')}`;
  }

  lessonMarkers(count: number): number[] {
    return Array.from({ length: count }, (_, index) => index);
  }

  formatDateLabel(value: unknown): string {
    const dateKey = this.normalizeDateKey(value);
    if (!dateKey) {
      return '—';
    }
    const [year, month, day] = dateKey.split('-');
    return `${day}.${month}.${year}`;
  }

  private selectInitialDay(schedules: ScheduleBase[]): void {
    const today = new Date();
    for (let offset = 0; offset < 35; offset += 1) {
      const candidate = new Date(today);
      candidate.setDate(today.getDate() + offset);
      const dateKey = this.toDateKey(candidate);
      const hasLesson = schedules.some(schedule =>
        schedule.weekDays.includes(this.weekdayFromDateKey(dateKey))
          && this.lessonEndDate(schedule, dateKey) > new Date()
      );
      if (hasLesson) {
        this.monthCursor.set(this.startOfMonth(candidate));
        this.selectedDate.set(dateKey);
        return;
      }
    }
  }

  private buildCalendarDays(month: Date): CalendarDay[] {
    const firstDay = new Date(month.getFullYear(), month.getMonth(), 1);
    const startOffset = (firstDay.getDay() + 6) % 7;
    const gridStart = new Date(firstDay);
    gridStart.setDate(firstDay.getDate() - startOffset);

    return Array.from({ length: 42 }, (_, index) => {
      const date = new Date(gridStart);
      date.setDate(gridStart.getDate() + index);
      const dateKey = this.toDateKey(date);
      const lessons = this.lessonsForDate(dateKey);
      const temporaryLessons = this.temporaryReplacementsForDate(dateKey);
      const allLessonSchedules = [
        ...lessons,
        ...temporaryLessons.map(item => item.scheduleBaseDtoRequest).filter(Boolean)
      ];

      return {
        date,
        dateKey,
        dayNumber: date.getDate(),
        inCurrentMonth: date.getMonth() === month.getMonth(),
        isToday: dateKey === this.toDateKey(new Date()),
        lessonsCount: lessons.length,
        temporaryLessonsCount: temporaryLessons.length,
        hasPastLessonsOnly: allLessonSchedules.length > 0
          && allLessonSchedules.every(schedule => this.lessonEndDate(schedule, dateKey) <= new Date())
      };
    });
  }

  private startOfMonth(date: Date): Date {
    return new Date(date.getFullYear(), date.getMonth(), 1);
  }

  private toDateKey(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private normalizeDateKey(value: unknown): string | null {
    if (typeof value === 'string') {
      return value.slice(0, 10);
    }

    if (value instanceof Date) {
      return this.toDateKey(value);
    }

    if (Array.isArray(value) && value.length >= 3) {
      const [year, month, day] = value;
      return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    }

    return null;
  }

  private weekdayFromDateKey(dateKey: string): WeekDay {
    const date = new Date(`${dateKey}T00:00:00`);
    return DAY_ORDER[(date.getDay() + 6) % 7];
  }

  private lessonEndDate(schedule: ScheduleBase, dateKey: string): Date {
    const end = schedule.endTime ?? [23, 59];
    const date = new Date(`${dateKey}T00:00:00`);
    date.setHours(end[0] ?? 23, end[1] ?? 59, 0, 0);
    return date;
  }

  private minutes(time: number[] | undefined): number {
    if (!time || time.length < 2) {
      return 0;
    }
    return time[0] * 60 + time[1];
  }
}
