import { Component, OnInit, signal } from "@angular/core";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { UserService } from "../../../core/services/user.service";
import { Router, RouterModule } from "@angular/router";
import { UserRole } from "../../../core/models/users/user.role.model";
import { CommonModule } from "@angular/common";

@Component({
    selector: 'app-ser-create',
    standalone: true,
    imports: [RouterModule, CommonModule, ReactiveFormsModule],
    templateUrl: './user-create-form.html'
})
export class UserCreate {

    loading = false;
    previewUrl = signal<string | null>(null);
    imageError = signal<string | null>(null);
    form;

    constructor(
        private formB: FormBuilder,
        private userService: UserService,
        private router: Router
    ) {
        this.form = this.formB.nonNullable.group({
            name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(40)]],
            surname: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(40)]],
            password: ['', [Validators.required, Validators.minLength(6)]],
            email: ['', [Validators.required, Validators.email, Validators.minLength(8), Validators.maxLength(40)]],
            profilePicture: [''],
            role: ['TEACHER' as UserRole, Validators.required]
        });

    }

    submit(): void {
    this.loading = true;

    this.userService.create(this.form.value).subscribe({
        next: () => {
            this.router.navigate(['/users'])
        },
        error: err => {
            console.log('ERROR FROM BACKEND 👉', err);
            if (err.status === 400 && err.error) {
                this.showErrors(err.error);
            }
        }
    });
}

    showErrors(errors: Record<string, string>) {
        Object.keys(errors).forEach(field => {
            const control = this.form.get(field)
            if (control) {
                control.setErrors({
                    ...control.errors,
                    backend: errors[field]
                });

                control.markAsTouched()
            }
        })
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
            const result = typeof reader.result === 'string' ? reader.result : '';
            this.form.patchValue({ profilePicture: result });
            this.previewUrl.set(result || null);
            this.imageError.set(null);
        };
        reader.onerror = () => this.imageError.set('Не удалось прочитать изображение.');
        reader.readAsDataURL(file);
    }

    clearProfilePicture(): void {
        this.form.patchValue({ profilePicture: '' });
        this.previewUrl.set(null);
        this.imageError.set(null);
    }
}
