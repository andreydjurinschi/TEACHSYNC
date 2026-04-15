import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CourseService } from '../../../core/services/course.service';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryBase } from '../../../core/models/category/category.model';
import { CourseBase } from '../../../core/models/courses/course.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-course-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './course-edit.html',
})
export class CourseEdit implements OnInit {
  form!: FormGroup;
  course = signal<CourseBase | null>(null);
  categories = signal<CategoryBase[]>([]);
  loading = signal(false);

  private platformId = inject(PLATFORM_ID);
  route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private courseService = inject(CourseService);
  private categoryService = inject(CategoryService);

  ngOnInit(): void {
    this.form = this.fb.group({
      name:        ['', [Validators.required, Validators.minLength(3)]],
      description: ['', Validators.required],
      categoryId:  [null],
    });

    if (!isPlatformBrowser(this.platformId)) return;

    const id = Number(this.route.snapshot.paramMap.get('id'));

    forkJoin({
      course:     this.courseService.getById(id),
      categories: this.categoryService.getAll(),
    }).subscribe({
      next: ({ course, categories }) => {
        this.course.set(course);
        this.categories.set(categories);
        this.form.patchValue({
          name:        course.name,
          description: course.description,
          categoryId:  course.categoryId ?? null,
        });
        
      },
      error: err => console.error(err),
    });
  }

submit(): void {
  if (this.form.invalid) return;
  const id = this.course()?.id;
  if (!id) return;
  this.loading.set(true);
  this.courseService.update(id, this.form.value).subscribe({
    next: () => this.router.navigate(['/courses', id]),
    error: () => this.loading.set(false),
  });
}
}