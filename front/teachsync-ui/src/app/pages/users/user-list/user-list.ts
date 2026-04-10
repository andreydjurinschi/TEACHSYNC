import {
  CommonModule,
  isPlatformBrowser,
} from '@angular/common';
import {
  Component,
  inject,
  OnInit,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import { RouterLink } from '@angular/router';

import { User } from '../../../core/models/users/user.model';
import { UserService } from '../../../core/services/user.service';

@Component({
  selector: 'app-user-list',
  imports: [CommonModule, RouterLink],
  standalone: true,
  templateUrl: './user-list.html',
  styleUrl: './user-list.css',
})
export class UserList implements OnInit {

  users = signal<User[]>([]);
  private platformId = inject(PLATFORM_ID);

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadUsers();
    }
  }

  loadUsers(): void {
      console.log('токен из localStorage:', localStorage.getItem('jwt_token'));
    this.userService.getAll().subscribe({
      next: data => {
        this.users.set(data);
        console.log("users after set:", this.users().length);
      },
      error: err => console.error(err)
    });
  }
}
