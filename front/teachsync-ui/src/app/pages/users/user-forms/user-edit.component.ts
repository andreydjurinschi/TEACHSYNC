import {
  Component,
  OnInit,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  ActivatedRoute,
  Router,
  RouterModule,
} from '@angular/router';

import { User } from '../../../core/models/users/user.model';
import { UserRole } from '../../../core/models/users/user.role.model';
import { UserService } from '../../../core/services/user.service';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit-form.html',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule],
})
export class UserEdit implements OnInit {
  user = signal<User | null>(null);
  loading = signal<boolean>(false);
  form;
  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private userService: UserService,
    private router: Router,
  ) {
    this.form = this.fb.nonNullable.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', Validators.required],
      role: ['TEACHER' as UserRole, Validators.required],
    });
  }

  ngOnInit(): void {
    const userId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadUser(userId);
  }

  loadUser(id: number): void {
    this.loading.set(true);
    this.userService.getById(id).subscribe({
      next: (user) => {
        this.user.set(user);
        this.form.patchValue({
          name: user.name ?? '',
          surname: user.surname ?? '',
          email: user.email ?? '',
          role: user.role ?? 'TEACHER',
        });
      },
      error: (err) => console.error('trablik', err),
      complete: () => this.loading.set(false),
    });
  }

  submit(): void {
    if (this.form.invalid || !this.user()) return;

    const id = this.user()!.id;

    this.userService.update(id, this.form.value).subscribe({
      next: () => {
        this.router.navigate(['/users', id]);
      },
      error: (err) => console.error('Error updating user:', err),
    });
  }
}
