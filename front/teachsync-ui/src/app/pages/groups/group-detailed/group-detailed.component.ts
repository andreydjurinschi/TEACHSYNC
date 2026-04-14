import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { GroupBase} from '../../../core/models/groups/group.model';
import { GroupWithCourses} from '../../../core/models/groups/group-course.model';
import { GroupService } from '../../../core/services/group.service';

@Component({
  selector: 'app-group-detailed',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './group-detailed.html',
})
export class GroupDetailed implements OnInit {
  group = signal<GroupBase | null>(null);
  withCourses = signal<GroupWithCourses | null>(null);

  private platformId = inject(PLATFORM_ID);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private groupService = inject(GroupService);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      const id = Number(this.route.snapshot.paramMap.get('id'));
      this.groupService.getById(id).subscribe({ next: data => this.group.set(data) });
      this.groupService.getWithCourses(id).subscribe({ next: data => this.withCourses.set(data) });
    }
  }

  delete(): void {
    const id = this.group()?.id;
    if (!id) return;
    this.groupService.delete(id).subscribe({
      next: () => this.router.navigate(['/groups']),
    });
  }
}