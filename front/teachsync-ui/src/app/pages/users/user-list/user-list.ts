import {
  CommonModule,
  isPlatformBrowser,
} from '@angular/common';
import {
  Component,
  computed,
  effect,
  inject,
  OnInit,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import {  RouterLink } from '@angular/router';

import { User } from '../../../core/models/users/user.model';
import { UserService } from '../../../core/services/user.service';
import { PaginationControlsComponent } from '../../../shared/pagination/pagination-controls.component';
import { getTotalPages, paginateItems } from '../../../shared/pagination/pagination.utils';

@Component({
  selector: 'app-user-list',
  imports: [CommonModule, RouterLink, PaginationControlsComponent],
  standalone: true,
  templateUrl: './user-list.html',
})
export class UserList implements OnInit {

  users = signal<User[]>([]);
  searchQuery = signal('');
  currentPage = signal(1);
  private readonly pageSize = 10;
  filteredUsers = computed(() => {
    const query = this.searchQuery().trim().toLowerCase();
    if (!query) {
      return this.users();
    }
    return this.users().filter(user =>
      user.name.toLowerCase().includes(query)
      || user.surname.toLowerCase().includes(query)
      || user.email.toLowerCase().includes(query)
    );
  });
  totalPages = computed(() => getTotalPages(this.filteredUsers().length, this.pageSize));
  visibleUsers = computed(() => paginateItems(this.filteredUsers(), this.currentPage(), this.pageSize));

  private platformId = inject(PLATFORM_ID);

  constructor(private userService: UserService,
  ) {
    effect(() => {
      const maxPage = this.totalPages();
      if (this.currentPage() > maxPage) {
        this.currentPage.set(maxPage);
      }
    });
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadUsers();
    }
  }

  loadUsers(): void {
    this.userService.getAll().subscribe({
      next: data => {
        this.users.set(data);
        this.currentPage.set(1);
      },
      error: err => console.error(err)
    });
  }

  updateSearch(query: string): void {
    this.searchQuery.set(query);
    this.currentPage.set(1);
  }
}
