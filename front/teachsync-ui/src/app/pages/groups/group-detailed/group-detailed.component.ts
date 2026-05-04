import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, computed, effect, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { GroupBase} from '../../../core/models/groups/group.model';
import { GroupWithCourses} from '../../../core/models/groups/group-course.model';
import { GroupService } from '../../../core/services/group.service';
import { PaginationControlsComponent } from '../../../shared/pagination/pagination-controls.component';
import { getTotalPages, paginateItems } from '../../../shared/pagination/pagination.utils';

@Component({
  selector: 'app-group-detailed',
  standalone: true,
  imports: [CommonModule, RouterLink, PaginationControlsComponent],
  templateUrl: './group-detailed.html',
})
export class GroupDetailed implements OnInit {
  group = signal<GroupBase | null>(null);
  withCourses = signal<GroupWithCourses | null>(null); 
  selectedCourseId = signal<number | null>(null); 
  currentPage = signal(1);
  private readonly pageSize = 6;
  courses = computed(() => this.withCourses()?.courses ?? []);
  totalPages = computed(() => getTotalPages(this.courses().length, this.pageSize));
  visibleCourses = computed(() => paginateItems(this.courses(), this.currentPage(), this.pageSize));

  private platformId = inject(PLATFORM_ID);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private groupService = inject(GroupService);

  constructor() {
    effect(() => {
      const maxPage = this.totalPages();
      if (this.currentPage() > maxPage) {
        this.currentPage.set(maxPage);
      }
    });
  }

  toggleCourse(id: number){
      this.selectedCourseId.set(this.selectedCourseId() === id ? null : id)
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      const id = Number(this.route.snapshot.paramMap.get('id'));
      this.groupService.getById(id).subscribe({ next: data => this.group.set(data) });
      this.groupService.getWithCourses(id).subscribe({
        next: data => {
          this.withCourses.set(data);
          this.currentPage.set(1);
        }
      });
    }
  }

  delete(): void {
    const id = this.group()?.id;
    if (!id) return;
    if (!confirm('Закрыть и удалить группу? Связанные занятия и заявки на замену будут очищены.')) {
      return;
    }
    this.groupService.delete(id).subscribe({
      next: () => this.router.navigate(['/groups']),
    });
  }
}
