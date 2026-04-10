import { Routes } from '@angular/router';

import { AuthGuard } from './core/auth/AuthGuard';
import { LayoutComponent } from './pages/layout/layout.component';
import { LoginComponent } from './pages/login/login-page';
import { UserDetailed } from './pages/users/user-detailed/user-detailed';
import { UserCreate } from './pages/users/user-forms/user-create.component';
import { UserEdit } from './pages/users/user-forms/user-edit.component';
import { UserList } from './pages/users/user-list/user-list';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'users', component: UserList },
      { path: 'users/create', component: UserCreate },
      { path: 'users/edit/:id', component: UserEdit },
      { path: 'users/:id', component: UserDetailed },
    ]
  },
  { path: '**', redirectTo: '/login' }
];