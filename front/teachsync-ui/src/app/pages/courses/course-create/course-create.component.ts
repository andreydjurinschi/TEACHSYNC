import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CourseService } from '../../../core/services/course.service';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryBase } from '../../../core/models/category/category.model';
import { ImageBase64Service } from '../../../core/services/image-base64.service';
import { RuleService } from '../../../core/services/role.rule.service';

@Component({
  selector: 'app-course-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './course-create.html',
})
export class CourseCreate implements OnInit {
  form!: FormGroup;
  loading = signal(false);
  error = signal<string | null>(null);
  categories = signal<CategoryBase[]>([]);
  categoryName = signal('');
  categorySaving = signal(false);
  categoryError = signal<string | null>(null);

  private platformId = inject(PLATFORM_ID);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private courseService = inject(CourseService);
  private categoryService = inject(CategoryService);
  private imageBase64Service = inject(ImageBase64Service);
  readonly ruleService = inject(RuleService);

  ngOnInit(): void {
    this.form = this.fb.group({
      name:        ['', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
      description: ['', [Validators.required, Validators.minLength(15), Validators.maxLength(200)]],
      categoryId:  [null],
      photoUrl:    [''],
    });

    if (isPlatformBrowser(this.platformId)) {
      this.loadCategories();
    }
  }

  loadCategories(): void {
    this.categoryService.getAll().subscribe({
      next: data => this.categories.set(data),
    });
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
    this.error.set(null);
    this.courseService.create(this.form.value).subscribe({
      next: () => this.router.navigate(['/courses']),
      error: err => {
        this.loading.set(false);
        const payload = err?.error;
        if (payload?.name) {
          this.error.set(`Название: ${payload.name}`);
          return;
        }
        if (payload?.description) {
          this.error.set(`Описание: ${payload.description}`);
          return;
        }
        this.error.set(typeof payload === 'string' ? payload : 'Не удалось создать курс.');
      },
    });
  }

  createCategory(): void {
    const name = this.categoryName().trim();
    if (!name) {
      this.categoryError.set('Введите название категории.');
      return;
    }
    this.categorySaving.set(true);
    this.categoryError.set(null);
    this.categoryService.create({ name }).subscribe({
      next: () => {
        this.categoryName.set('');
        this.categorySaving.set(false);
        this.loadCategories();
      },
      error: err => {
        this.categorySaving.set(false);
        this.categoryError.set(err?.error?.message ?? 'Не удалось создать категорию.');
      }
    });
  }
}
