import { Routes } from '@angular/router';
import { UserList } from './pages/users/user-list/user-list';
import { UserDetailed } from './pages/users/user-detailed/user-detailed';
import { UserEdit } from './pages/users/user-forms/user-edit.component';
import { UserCreate } from './pages/users/user-forms/user-create.component';

export const routes: Routes = [
  { path: 'users', component: UserList },
  { path: 'users/create', component: UserCreate},
  { path: 'users/edit/:id',component: UserEdit },
  { path: 'users/:id', component: UserDetailed },
  { path: '', redirectTo: 'users', pathMatch: 'full' }
];
