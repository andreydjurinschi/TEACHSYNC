import { Component, OnInit, signal } from '@angular/core';
import { User } from '../../../core/models/users/user.model';
import { UserService } from '../../../core/services/user.service';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-user-list',
  imports: [CommonModule, RouterLink],
  standalone: true,
  templateUrl: './user-list.html',
  styleUrl: './user-list.css',
})
export class UserList implements OnInit {

  users = signal<User[]>([]);  

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.getAll().subscribe({
      next: data => {
        this.users.set(data);   
        console.log("users after set:", this.users().length);
      },
      error: err => console.error(err)
    });
  }
}
