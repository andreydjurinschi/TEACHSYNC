import { CommonModule, isPlatformBrowser } from "@angular/common";
import { Component, computed, effect, inject, OnInit, PLATFORM_ID, signal } from "@angular/core";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { CourseService } from "../../../core/services/course.service";
import { GroupService } from "../../../core/services/group.service";
import { CourseBase } from "../../../core/models/courses/course.model";
import { GroupBase } from "../../../core/models/groups/group.model";
import { RuleService } from "../../../core/services/role.rule.service";
import { PaginationControlsComponent } from "../../../shared/pagination/pagination-controls.component";
import { getTotalPages, paginateItems } from "../../../shared/pagination/pagination.utils";

@Component({
  standalone: true,
  templateUrl: './course-groups.html',
  imports: [CommonModule, RouterLink, PaginationControlsComponent],
  selector: 'app-course-groups'
})
export class CourseGroups implements OnInit {
  courseId!: number;
  courseData = signal<CourseBase | null>(null);
  allGroups = signal<GroupBase[]>([]);
  assignedGroups = signal<GroupBase[]>([]);
  loading = signal(false);
  assignedPage = signal(1);
  availablePage = signal(1);
  private readonly pageSize = 8;
  assignedTotalPages = computed(() => getTotalPages(this.assignedGroups().length, this.pageSize));
  availableTotalPages = computed(() => getTotalPages(this.availableGroups().length, this.pageSize));
  visibleAssignedGroups = computed(() => paginateItems(this.assignedGroups(), this.assignedPage(), this.pageSize));
  visibleAvailableGroups = computed(() => paginateItems(this.availableGroups(), this.availablePage(), this.pageSize));

  private platformId = inject(PLATFORM_ID);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private courseService = inject(CourseService);
  private groupService = inject(GroupService);
  private ruleService = inject(RuleService);

  constructor() {
    effect(() => {
      const maxPage = this.assignedTotalPages();
      if (this.assignedPage() > maxPage) {
        this.assignedPage.set(maxPage);
      }
    });
    effect(() => {
      const maxPage = this.availableTotalPages();
      if (this.availablePage() > maxPage) {
        this.availablePage.set(maxPage);
      }
    });
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.courseId = Number(this.route.snapshot.paramMap.get('id'));
      if (!(this.ruleService.isAdmin() || this.ruleService.isManager())) {
        this.router.navigate(['/courses', this.courseId]);
        return;
      }
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
            this.assignedPage.set(1);
            this.availablePage.set(1);
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

  availableGroups = computed(() => this.allGroups().filter(group => !this.isAssigned(group)));
}
