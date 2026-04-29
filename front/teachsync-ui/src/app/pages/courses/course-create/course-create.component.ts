import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CourseService } from '../../../core/services/course.service';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryBase } from '../../../core/models/category/category.model';

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
  previewUrl = signal<string | null>(null);
  imageError = signal<string | null>(null);

  private platformId = inject(PLATFORM_ID);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private courseService = inject(CourseService);
  private categoryService = inject(CategoryService);

  ngOnInit(): void {
    this.form = this.fb.group({
      name:        ['', [Validators.required, Validators.minLength(3)]],
      description: ['', Validators.required],
      categoryId:  [null],
      photoUrl:    [null],
    });

    if (isPlatformBrowser(this.platformId)) {
      this.categoryService.getAll().subscribe({
        next: data => this.categories.set(data),
      });
    }
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.courseService.create(this.form.value).subscribe({
      next: () => this.router.navigate(['/courses']),
      error: () => this.loading.set(false),
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      this.imageError.set('Можно загружать только изображения.');
      input.value = '';
      return;
    }
    if (file.size > 2 * 1024 * 1024) {
      this.imageError.set('Максимальный размер изображения — 2 МБ.');
      input.value = '';
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const result = typeof reader.result === 'string' ? reader.result : null;
      this.form.patchValue({ photoUrl: result });
      this.previewUrl.set(result);
      this.imageError.set(null);
    };
    reader.onerror = () => {
      this.imageError.set('Не удалось прочитать изображение.');
    };
    reader.readAsDataURL(file);
  }

  clearPhoto(): void {
    this.form.patchValue({ photoUrl: null });
    this.previewUrl.set(null);
    this.imageError.set(null);
  }
}
