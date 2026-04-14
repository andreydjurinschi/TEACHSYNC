import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CourseService } from '../../../core/services/course.service';
import { TopicService } from '../../../core/services/topic.service';
import { Topic } from '../../../core/models/topics/topic.model';
import { CourseBase } from '../../../core/models/courses/course.model';

@Component({
  selector: 'app-course-topics',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './course-topics.html',
})
export class CourseTopics implements OnInit {
  courseId!: number;
  courseName = signal<CourseBase | null>(null);
  allTopics = signal<Topic[]>([]);
  assignedTopics = signal<Topic[]>([]);
  loading = signal(false);

  private platformId = inject(PLATFORM_ID);
  private route = inject(ActivatedRoute);
  private courseService = inject(CourseService);
  private topicService = inject(TopicService);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.courseId = Number(this.route.snapshot.paramMap.get('id'));
      this.courseService.getById(this.courseId).subscribe({
        next: data => {
          this.courseName.set(data);
        },
        error: err => console.error(err),
      });
      this.loadData();
    }
  }

loadData(): void {
  this.topicService.getAll().subscribe({
    next: allTopics => {
      this.allTopics.set(allTopics);

      this.courseService.getDetailed(this.courseId).subscribe({
        next: data => {
          const assigned = (data.topics ?? []).map(t => {
            const found = allTopics.find(at => at.name === t.name);
            return { id: found?.id ?? 0, name: t.name };
          });
          this.assignedTopics.set(assigned);
        }
      });
    }
  });
}

  isAssigned(topic: Topic): boolean {
    return this.assignedTopics().some(t => t.name === topic.name);
  }

  assign(topic: Topic): void {
    this.loading.set(true);
    this.courseService.assignTopic(this.courseId, topic.id).subscribe({
      next: () => { this.loadData(); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  unassign(topic: Topic): void {
    this.loading.set(true);
    this.courseService.unassignTopic(this.courseId, topic.id).subscribe({
      next: () => { this.loadData(); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}