import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CourseService } from '../../../core/services/course.service';
import { CourseBase } from '../../../core/models/courses/course.model';

@Component({
  selector: 'app-course-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './course-edit.html',
})
export class CourseEdit implements OnInit {
  form!: FormGroup;
  course = signal<CourseBase | null>(null);
  loading = signal(false);

  private platformId = inject(PLATFORM_ID);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private courseService = inject(CourseService);

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
      description: ['', [Validators.required, Validators.minLength(15), Validators.maxLength(200)]],
      photoUrl: [''],
    });

    if (isPlatformBrowser(this.platformId)) {
      const id = Number(this.route.snapshot.paramMap.get('id'));
      this.courseService.getById(id).subscribe({
        next: data => {
          this.course.set(data);
          this.form.patchValue({
            name: data.name,
            description: data.description,
            photoUrl: data.photoUrl,
          });
        },
        error: err => console.error(err),
      });
    }
  }

  submit(): void {
    if (this.form.invalid) return;
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loading.set(true);
    this.courseService.update(id, this.form.value).subscribe({
      next: () => this.router.navigate(['/courses', id]),
      error: err => {
        console.error(err);
        this.loading.set(false);
      },
    });
  }
}