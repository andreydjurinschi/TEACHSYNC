import { Component, OnInit, signal, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ScheduleService } from '../../../../core/services/schedule.service';
import { ScheduleBase } from '../../../../core/models/schedules/schedule-base.model';
import { WeekDay } from '../../../../core/models/schedules/schedule-base.model';
import { ReplacementRequest } from '../../../../core/models/replacements/replacement.model';
import { ReplacementService } from '../../../../core/services/replacement.service';
import { RuleService } from '../../../../core/services/role.rule.service';
import { ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';

const DAY_ORDER = ['MON','TUE','WED','THU','FRI','SAT','SUN'];

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
  loading   = signal(true);
  activeDay = signal<WeekDay | 'all'>('all');
  actionMessage = signal<string | null>(null);
  actionError = signal<string | null>(null);
  selectedSchedule = signal<ScheduleBase | null>(null);
  selectedReplacementDay = signal<WeekDay | null>(null);
  replacementWeeks = 1;
  replacementReason = '';
  submittingReplacement = signal(false);

readonly DAY_ORDER: WeekDay[] = ['MON','TUE','WED','THU','FRI','SAT','SUN'];

  readonly DAY_LABELS: Record<string, string> = {
    MON: 'Пн', TUE: 'Вт', WED: 'Ср',
    THU: 'Чт', FRI: 'Пт', SAT: 'Сб', SUN: 'Вс'
  };

  readonly todayKey: string = DAY_ORDER[((new Date().getDay() + 6) % 7)];

  ngOnInit(): void {
    this.scheduleService.getMySchedule().subscribe({
      next: d => { this.schedules.set(d); this.loading.set(false); },
      error: () => this.loading.set(false)
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
    if (teacherId == null) return;
    this.replacementService.getForTeacher(teacherId).subscribe({
      next: data => this.replacements.set(data),
      error: () => this.replacements.set([])
    });
  }


slotsForDay(day: WeekDay | 'all'): ScheduleBase[] {
  if (day === 'all') return [];
  return this.schedules()
    .filter(s => s.weekDays.includes(day))
    .sort((a, b) => {
      const ta = a.startTime[0] * 60 + a.startTime[1];
      const tb = b.startTime[0] * 60 + b.startTime[1];
      return ta - tb;
    });
}
setDay(day: WeekDay | 'all'): void {
  this.activeDay.set(day);
}

  formatTime(t: number[]): string {
    if (!t) return '—';
    return `${String(t[0]).padStart(2,'0')}:${String(t[1]).padStart(2,'0')}`;
  }

  sortedDays(days: string[]): string[] {
    return [...days].sort((a, b) => DAY_ORDER.indexOf(a) - DAY_ORDER.indexOf(b));
  }

  openReplacementRequest(schedule: ScheduleBase, day: WeekDay): void {
    this.selectedSchedule.set(schedule);
    this.selectedReplacementDay.set(day);
    this.replacementWeeks = 1;
    this.replacementReason = '';
    this.actionMessage.set(null);
    this.actionError.set(null);
  }

  closeReplacementRequest(): void {
    this.selectedSchedule.set(null);
    this.selectedReplacementDay.set(null);
    this.replacementWeeks = 1;
    this.replacementReason = '';
    this.submittingReplacement.set(false);
  }

  requestReplacement(): void {
    const teacherId = this.ruleService.getId();
    const schedule = this.selectedSchedule();
    const day = this.selectedReplacementDay();
    const reason = this.replacementReason.trim();
    if (teacherId == null || schedule == null || day == null) return;
    if (!reason) {
      this.actionError.set('Укажите причину отсутствия.');
      return;
    }
    const lessonDates = this.nextLessonDates(schedule, day, this.replacementWeeks);
    if (!lessonDates.length) {
      this.actionError.set('Не удалось определить даты занятий для замены.');
      return;
    }

    this.actionMessage.set(null);
    this.actionError.set(null);
    this.submittingReplacement.set(true);

    forkJoin(
      lessonDates.map(lessonDate => this.replacementService.create({
        scheduleId: schedule.id,
        teacherRequested: teacherId,
        lessonDate,
        reason
      }))
    ).subscribe({
      next: requests => {
        const requestMap = new Map<number, ReplacementRequest>();
        [...requests, ...this.replacements()].forEach(item => requestMap.set(item.id, item));
        this.replacements.set(Array.from(requestMap.values()));
        this.actionMessage.set(`Создано заявок на замену: ${requests.length}. Свободные преподаватели с подходящей специализацией получили уведомления.`);
        this.submittingReplacement.set(false);
        this.closeReplacementRequest();
      },
      error: err => {
        this.submittingReplacement.set(false);
        this.actionError.set(err?.error?.message ?? 'Не удалось создать заявки на замену.');
      }
    });
  }

  nextLessonDate(schedule: ScheduleBase, day: WeekDay): string | null {
    const now = new Date();

    for (let offset = 0; offset < 21; offset += 1) {
      const candidate = new Date(now);
      candidate.setHours(0, 0, 0, 0);
      candidate.setDate(now.getDate() + offset);

      const weekdayKey = DAY_ORDER[(candidate.getDay() + 6) % 7] as WeekDay;
      if (weekdayKey !== day || !schedule.weekDays.includes(weekdayKey)) {
        continue;
      }

      const lessonEnd = new Date(candidate);
      lessonEnd.setHours(schedule.endTime[0] ?? 23, schedule.endTime[1] ?? 59, 0, 0);
      if (lessonEnd <= now) {
        continue;
      }

      return candidate.toISOString().slice(0, 10);
    }

    return null;
  }

  nextLessonDates(schedule: ScheduleBase, day: WeekDay, weeks: number): string[] {
    const firstDate = this.nextLessonDate(schedule, day);
    if (!firstDate) {
      return [];
    }

    const [year, month, date] = firstDate.split('-').map(Number);
    const start = new Date(year, month - 1, date);
    const lessonDates: string[] = [];

    for (let index = 0; index < weeks; index += 1) {
      const candidate = new Date(start);
      candidate.setDate(start.getDate() + (index * 7));
      lessonDates.push(candidate.toISOString().slice(0, 10));
    }

    return lessonDates;
  }

  nextLessonDateLabel(schedule: ScheduleBase, weekDay: WeekDay): string {
    const lessonDate = this.nextLessonDate(schedule, weekDay);
    if (!lessonDate) return 'ближайшую пару';
    const [year, month, dayOfMonth] = lessonDate.split('-');
    return `${dayOfMonth}.${month}.${year}`;
  }

  replacementPreviewDates(): string[] {
    const schedule = this.selectedSchedule();
    const day = this.selectedReplacementDay();
    if (schedule == null || day == null) {
      return [];
    }

    return this.nextLessonDates(schedule, day, this.replacementWeeks)
      .map(item => {
        const [year, month, date] = item.split('-');
        return `${date}.${month}.${year}`;
      });
  }

  approveReplacement(requestId: number): void {
    const teacherId = this.ruleService.getId();
    if (teacherId == null) return;
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

  replacementSummary(): ReplacementRequest[] {
    const teacherId = this.ruleService.getId();
    return this.replacements().filter(item =>
      item.teacherBaseInfoRequest?.id === teacherId || item.approvedByTeacherBaseInfoRequest?.id === teacherId
    );
  }

  teacherName(teacher?: { name?: string; surname?: string; fullName?: string }): string {
    if (!teacher) return '—';
    return teacher.fullName || `${teacher.name ?? ''} ${teacher.surname ?? ''}`.trim();
  }

  replacementStatusLabel(status: ReplacementRequest['status']): string {
    const map: Record<ReplacementRequest['status'], string> = {
      PENDING: 'Поиск',
      APPROVED: 'Подтверждена',
      DECLINED: 'Отклонена',
      EXPIRED: 'Просрочена',
      CANCELLED: 'Отменена',
      AUTO_CLOSED: 'Автозакрыта'
    };
    return map[status];
  }
}
