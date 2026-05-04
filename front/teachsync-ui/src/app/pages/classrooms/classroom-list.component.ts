import { CommonModule } from '@angular/common';
import { Component, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ClassRoomInfo } from '../../core/models/classrooms/classroom-info.model';
import { ImageBase64Service } from '../../core/services/image-base64.service';
import { RuleService } from '../../core/services/role.rule.service';
import { ScheduleService } from '../../core/services/schedule.service';
import { PaginationControlsComponent } from '../../shared/pagination/pagination-controls.component';
import { getTotalPages, paginateItems } from '../../shared/pagination/pagination.utils';

@Component({
  selector: 'app-classroom-list',
  standalone: true,
  imports: [CommonModule, FormsModule, PaginationControlsComponent],
  templateUrl: './classroom-list.component.html',
})
export class ClassroomListComponent {
  private scheduleService = inject(ScheduleService);
  private imageBase64Service = inject(ImageBase64Service);
  readonly ruleService = inject(RuleService);

  classrooms = signal<ClassRoomInfo[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  currentPage = signal(1);
  readonly pageSize = 8;

  form = signal<{ id: number | null; name: string; capacity: number | null; photoUrl: string }>({
    id: null,
    name: '',
    capacity: null,
    photoUrl: '',
  });
  saving = signal(false);

  totalPages = signal(1);
  visibleClassrooms = signal<ClassRoomInfo[]>([]);

  constructor() {
    effect(() => {
      const items = this.classrooms();
      const total = getTotalPages(items.length, this.pageSize);
      this.totalPages.set(total);
      if (this.currentPage() > total) {
        this.currentPage.set(total);
      }
      this.visibleClassrooms.set(paginateItems(items, this.currentPage(), this.pageSize));
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.scheduleService.getAllClassrooms().subscribe({
      next: data => {
        this.classrooms.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Не удалось загрузить аудитории.');
        this.loading.set(false);
      }
    });
  }

  edit(item: ClassRoomInfo): void {
    this.form.set({
      id: item.id,
      name: item.name,
      capacity: item.capacity,
      photoUrl: item.photoUrl ?? '',
    });
  }

  resetForm(): void {
    this.form.set({ id: null, name: '', capacity: null, photoUrl: '' });
  }

  updateName(name: string): void {
    this.form.update(value => ({ ...value, name }));
  }

  updateCapacity(rawValue: string | number): void {
    const value = typeof rawValue === 'number' ? rawValue : rawValue === '' ? null : +rawValue;
    this.form.update(current => ({ ...current, capacity: value }));
  }

  async onPhotoSelected(event: Event): Promise<void> {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) {
      input.value = '';
      return;
    }
    const dataUrl = await this.imageBase64Service.toDataUrl(file);
    this.form.update(value => ({ ...value, photoUrl: dataUrl }));
  }

  submit(): void {
    const value = this.form();
    if (!value.name.trim() || value.capacity == null) {
      this.error.set('Заполните название и вместимость аудитории.');
      return;
    }
    this.saving.set(true);
    this.error.set(null);
    const request = value.id == null
      ? this.scheduleService.createClassroom({ name: value.name.trim(), capacity: value.capacity, photoUrl: value.photoUrl || undefined })
      : this.scheduleService.updateClassroom(value.id, { name: value.name.trim(), capacity: value.capacity, photoUrl: value.photoUrl || undefined });
    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.resetForm();
        this.load();
      },
      error: err => {
        this.saving.set(false);
        this.error.set(err?.error?.message ?? 'Не удалось сохранить аудиторию.');
      }
    });
  }

  remove(item: ClassRoomInfo): void {
    if (!confirm(`Удалить аудиторию "${item.name}"?`)) {
      return;
    }
    this.scheduleService.deleteClassroom(item.id).subscribe({
      next: () => this.load(),
      error: err => this.error.set(err?.error?.message ?? 'Не удалось удалить аудиторию.')
    });
  }
}
