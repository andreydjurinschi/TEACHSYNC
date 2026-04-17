import { CommonModule, isPlatformBrowser } from "@angular/common";
import { Component, inject, OnInit, PLATFORM_ID, signal } from "@angular/core";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { CourseService } from "../../../core/services/course.service";
import { GroupService } from "../../../core/services/group.service";
import { CourseBase } from "../../../core/models/courses/course.model";
import { GroupBase } from "../../../core/models/groups/group.model";

@Component({
  standalone: true,
  templateUrl: './course-groups.html',
  imports: [CommonModule, RouterLink],
  selector: 'app-course-groups'
})
export class CourseGroups implements OnInit {
  courseId!: number;
  courseData = signal<CourseBase | null>(null);
  allGroups = signal<GroupBase[]>([]);
  assignedGroups = signal<GroupBase[]>([]);
  loading = signal(false);

  private platformId = inject(PLATFORM_ID);
  private route = inject(ActivatedRoute);
  private courseService = inject(CourseService);
  private groupService = inject(GroupService);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.courseId = Number(this.route.snapshot.paramMap.get('id'));
      this.loadData();
    }
  }

  loadData(): void {
    this.loading.set(true);

    this.courseService.getById(this.courseId).subscribe({
      next: course => this.courseData.set(course),
      error: err => console.log(err)
    });

    this.groupService.getAll().subscribe({
      next: groups => {
        this.allGroups.set(groups);

        this.courseService.getWithGroups(this.courseId).subscribe({
          next: data => {
            const assigned = data.groups ?? [];
            this.assignedGroups.set(
              groups.filter(group => assigned.some(a => a.id === group.id))
            );
            this.loading.set(false);
          },
          error: err => {
            console.log(err);
            this.loading.set(false);
          }
        });
      },
      error: err => {
        console.log(err);
        this.loading.set(false);
      }
    });
  }

  isAssigned(group: GroupBase): boolean {
    return this.assignedGroups().some(g => g.id === group.id);
  }

  assign(group: GroupBase): void {
    if (this.loading() || this.isAssigned(group)) return;

    this.loading.set(true);
    this.courseService.assignGroup(this.courseId, group.id).subscribe({
      next: () => this.loadData(),
      error: err => {
        console.log(err);
        this.loading.set(false);
      }
    });
  }

  unassign(group: GroupBase): void {
    if (this.loading() || !this.isAssigned(group)) return;

    this.loading.set(true);
    this.courseService.unassignGroup(this.courseId, group.id).subscribe({
      next: () => this.loadData(),
      error: err => {
        console.log(err);
        this.loading.set(false);
      }
    });
  }

  get availableGroups(): GroupBase[] {
    return this.allGroups().filter(group => !this.isAssigned(group));
  }
}