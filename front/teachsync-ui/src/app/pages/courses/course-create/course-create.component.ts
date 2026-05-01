import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CourseService } from '../../../core/services/course.service';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryBase } from '../../../core/models/category/category.model';
import { ImageBase64Service } from '../../../core/services/image-base64.service';

@Component({
  selector: 'app-course-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './course-create.html',
})
export class CourseCreate implements OnInit {
  form!: FormGroup;
  loading = signal(false);
  categories = signal<CategoryBase[]>([]);

  private platformId = inject(PLATFORM_ID);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private courseService = inject(CourseService);
  private categoryService = inject(CategoryService);
  private imageBase64Service = inject(ImageBase64Service);

  ngOnInit(): void {
    this.form = this.fb.group({
      name:        ['', [Validators.required, Validators.minLength(3)]],
      description: ['', Validators.required],
      categoryId:  [null],
      photoUrl:    [''],
    });

    if (isPlatformBrowser(this.platformId)) {
      this.categoryService.getAll().subscribe({
        next: data => this.categories.set(data),
      });
    }
  }

  onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) {
      input.value = '';
      return;
    }
    this.imageBase64Service.toDataUrl(file).then(dataUrl => {
      this.form.patchValue({ photoUrl: dataUrl });
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.courseService.create(this.form.value).subscribe({
      next: () => this.router.navigate(['/courses']),
      error: () => this.loading.set(false),
    });
  }
}
