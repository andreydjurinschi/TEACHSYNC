import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CourseService } from '../../../core/services/course.service';
import { TopicService } from '../../../core/services/topic.service';
import { Topic } from '../../../core/models/topics/topic.model';
import { CourseBase } from '../../../core/models/courses/course.model';
import { TopicTag } from '../../../core/models/topics/topic.model';

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
  selectedTag = signal<TopicTag | 'ALL'>('ALL');
  filteredTopics = signal<Topic[]>([]);
  readonly tags: (TopicTag | 'ALL')[] = ['ALL', 'IT', 'DESIGN', 'MATH', 'LANGUAGE', 'BUSINESS', 'SCIENCE', 'OTHER'];

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
        this.applyFilter();

        this.courseService.getDetailed(this.courseId).subscribe({
          next: data => {
            const assigned = (data.topics ?? []).map(t => {
              const found = allTopics.find(at => at.name === t.name);
              return { id: found?.id ?? 0, name: t.name, topicTag: found?.topicTag ?? null };
            });
            this.assignedTopics.set(assigned);
          }
        });
      }
    });
  }

  applyFilter(): void {
    const tag = this.selectedTag();
    const all = this.allTopics();
    this.filteredTopics.set(tag === 'ALL' ? all : all.filter(t => t.topicTag === tag));
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

  selectTag(tag: TopicTag | 'ALL'): void {
    this.selectedTag.set(tag);
    this.applyFilter();
  }

  getTagColor(tag: TopicTag | null): string {
    const colors: Record<string, string> = {
      IT:       'bg-indigo-500/20 text-indigo-400',
      DESIGN:   'bg-pink-500/20 text-pink-400',
      MATH:     'bg-amber-500/20 text-amber-400',
      LANGUAGE: 'bg-emerald-500/20 text-emerald-400',
      BUSINESS: 'bg-blue-500/20 text-blue-400',
      SCIENCE:  'bg-purple-500/20 text-purple-400',
      OTHER:    'bg-slate-500/20 text-slate-400',
    };
    return tag ? (colors[tag] ?? 'bg-slate-500/20 text-slate-400') : 'bg-slate-500/20 text-slate-400';
  }
}