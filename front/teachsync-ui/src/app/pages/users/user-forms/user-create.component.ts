import { Component, OnInit } from "@angular/core";
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
    submitted = false;
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
            role: ['TEACHER' as UserRole, Validators.required]
        });

    }

    submit(): void {
    this.submitted = true;
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
}