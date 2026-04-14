import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { GroupBase } from '../../../core/models/groups/group.model';
import { GroupService } from '../../../core/services/group.service';

@Component({
  selector: 'app-group-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './group-list.html',
})
export class GroupList implements OnInit {
  groups = signal<GroupBase[]>([]);
  loading = signal(true);

  private platformId = inject(PLATFORM_ID);
  private router = inject(Router);
  private groupService = inject(GroupService);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.groupService.getAll().subscribe({
        next: data => { this.groups.set(data); this.loading.set(false); },
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