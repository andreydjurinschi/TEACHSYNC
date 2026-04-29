import { Component, OnInit, signal, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScheduleService } from '../../../../core/services/schedule.service';
import { ScheduleBase } from '../../../../core/models/schedules/schedule-base.model';
import { WeekDay } from '../../../../core/models/schedules/schedule-base.model';
import { ReplacementRequest } from '../../../../core/models/replacements/replacement.model';
import { ReplacementService } from '../../../../core/services/replacement.service';
import { RuleService } from '../../../../core/services/role.rule.service';
import { ActivatedRoute } from '@angular/router';

const DAY_ORDER = ['MON','TUE','WED','THU','FRI','SAT','SUN'];

@Component({
  selector: 'app-teacher-schedule',
  standalone: true,
  imports: [CommonModule],
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

  requestReplacement(schedule: ScheduleBase): void {
    const teacherId = this.ruleService.getId();
    if (teacherId == null) return;
    const lessonDate = window.prompt('Дата пары для замены в формате YYYY-MM-DD');
    if (!lessonDate) return;
    const reason = window.prompt('Причина замены') ?? 'Причина не указана';
    this.actionMessage.set(null);
    this.actionError.set(null);
    this.replacementService.create({
      scheduleId: schedule.id,
      teacherRequested: teacherId,
      lessonDate,
      reason
    }).subscribe({
      next: request => {
        this.actionMessage.set('Запрос замены создан. Свободные преподаватели с подходящей специализацией получили уведомления.');
        this.replacements.update(list => [request, ...list]);
      },
      error: err => this.actionError.set(err?.error?.message ?? 'Не удалось создать запрос замены.')
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
}
