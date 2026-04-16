import { CommonModule, isPlatformBrowser } from "@angular/common";
import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from "@angular/router";
import { GroupBase } from "../../../core/models/groups/group.model";
import { CourseBase } from "../../../core/models/courses/course.model";
import { GroupService } from "../../../core/services/group.service";
import { CourseService } from "../../../core/services/course.service";
import { forkJoin } from "rxjs";

@Component({
    selector: 'app-group-course',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './group-course.html'
})
export class GroupCourse implements OnInit {

    groupId!: number;
    groupData = signal<GroupBase | null>(null);
    allCourses = signal<CourseBase[]>([]);
    assignedIds = signal<Set<number>>(new Set());
    loadingId = signal<number | null>(null); 

    platformId = inject(PLATFORM_ID);
    route = inject(ActivatedRoute);
    groupService = inject(GroupService);
    courseService = inject(CourseService);

    ngOnInit(): void {
        if (!isPlatformBrowser(this.platformId)) return;

        this.groupId = Number(this.route.snapshot.paramMap.get("id"));

        forkJoin({
            group:   this.groupService.getById(this.groupId),
            courses: this.courseService.getAll(),
            assigned: this.groupService.getWithCourses(this.groupId),
        }).subscribe({
            next: ({ group, courses, assigned }) => {
                this.groupData.set(group);
                this.allCourses.set(courses);
                const ids = new Set<number>(assigned.courses.map((c: any) => c.id));
                this.assignedIds.set(ids);
            },
            error: err => console.error(err)
        });
    }

    isAssigned(courseId: number): boolean {
        return this.assignedIds().has(courseId);
    }

    assign(course: CourseBase): void {
        this.loadingId.set(course.id);
        this.groupService.assignToCourse(this.groupId, course.id).subscribe({
            next: () => {
                const updated = new Set(this.assignedIds());
                updated.add(course.id);
                this.assignedIds.set(updated);
                this.loadingId.set(null);
            },
            error: err => {
                console.error(err);
                this.loadingId.set(null);
            }
        });
    }

    unassign(course: CourseBase): void {
        this.loadingId.set(course.id);
        this.groupService.unassignFromCourse(this.groupId, course.id).subscribe({
            next: () => {
                const updated = new Set(this.assignedIds());
                updated.delete(course.id);
                this.assignedIds.set(updated);
                this.loadingId.set(null);
            },
            error: err => {
                console.error(err);
                this.loadingId.set(null);
            }
        });
    }
}