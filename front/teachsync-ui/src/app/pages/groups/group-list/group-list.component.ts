import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, computed, effect, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { GroupBase } from '../../../core/models/groups/group.model';
import { GroupService } from '../../../core/services/group.service';
import { PaginationControlsComponent } from '../../../shared/pagination/pagination-controls.component';
import { getTotalPages, paginateItems } from '../../../shared/pagination/pagination.utils';

@Component({
  selector: 'app-group-list',
  standalone: true,
  imports: [CommonModule, RouterLink, PaginationControlsComponent],
  templateUrl: './group-list.html',
})
export class GroupList implements OnInit {
  groups = signal<GroupBase[]>([]);
  loading = signal(true);
  currentPage = signal(1);
  private readonly pageSize = 10;
  totalPages = computed(() => getTotalPages(this.groups().length, this.pageSize));
  visibleGroups = computed(() => paginateItems(this.groups(), this.currentPage(), this.pageSize));

  private platformId = inject(PLATFORM_ID);
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

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.groupService.getAll().subscribe({
        next: data => { this.groups.set(data); this.currentPage.set(1); this.loading.set(false); },
        error: () => this.loading.set(false),
      });
    }
  }

  delete(id: number, event: Event): void {
    event.stopPropagation();
    this.groupService.delete(id).subscribe({
      next: () => this.groups.update(g => g.filter(x => x.id !== id)),
    });
  }

  goTo(group: GroupBase): void {
    this.router.navigate(['/groups', group.id]);
  }
}
