import { Routes } from '@angular/router';

import { AuthGuard } from './core/auth/AuthGuard';
import { LoginComponent } from './pages/login/login-page';
import { UserDetailed } from './pages/users/user-detailed/user-detailed';
import { UserCreate } from './pages/users/user-forms/user-create.component';
import { UserEdit } from './pages/users/user-forms/user-edit.component';
import { UserList } from './pages/users/user-list/user-list';

export const routes: Routes = [
  { path: 'users', component: UserList, canActivate: [AuthGuard] },
  { path: 'users/create', component: UserCreate, canActivate: [AuthGuard]},
  { path: 'users/edit/:id',component: UserEdit, canActivate: [AuthGuard] },
  { path: 'users/:id', component: UserDetailed, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent},
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];
