import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { GroupService } from '../../../core/services/group.service';

@Component({
  selector: 'app-group-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './group-create.html',
})
export class GroupCreate {
  form!: FormGroup;
  loading = signal(false);

  private fb = inject(FormBuilder);
  private router = inject(Router);
  private groupService = inject(GroupService);

  constructor() {
    this.form = this.fb.group({
      name:     ['', [Validators.required, Validators.minLength(4), Validators.maxLength(10)]],
      openDate: ['', Validators.required],
      capacity: [null, [Validators.required, Validators.min(12), Validators.max(35)]],
    });
  }

submit(): void {
  if (this.form.invalid) return;
  this.loading.set(true);
  this.groupService.create(this.form.value).subscribe({
    next: (res) => {
      console.log('success:', res);
      this.router.navigate(['/groups']);
    },
    error: (err) => {
      console.error('error:', err);
      this.loading.set(false);
    },
  });
}
}