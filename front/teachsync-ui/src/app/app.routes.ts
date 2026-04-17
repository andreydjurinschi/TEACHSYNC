import { Routes } from '@angular/router';

import { AuthGuard } from './core/auth/AuthGuard';
import { LayoutComponent } from './pages/layout/layout.component';
import { LoginComponent } from './pages/login/login-page';
import { UserDetailed } from './pages/users/user-detailed/user-detailed';
import { UserCreate } from './pages/users/user-forms/user-create.component';
import { UserEdit } from './pages/users/user-forms/user-edit.component';
import { UserList } from './pages/users/user-list/user-list';
import { CourseList } from './pages/courses/course-list/course-list.component';
import { CourseDetailed } from './pages/courses/course-detailed/course-detailed.component';
import { CourseEdit } from './pages/courses/course-edit/course-edit.component';
import { CourseTopics } from './pages/courses/course-topics/course-topics.component';
import { GroupList } from './pages/groups/group-list/group-list.component';
import { GroupDetailed } from './pages/groups/group-detailed/group-detailed.component';
import { GroupEdit } from './pages/groups/group-forms/group-edit.component';
import { GroupCreate } from './pages/groups/group-forms/group-create.component';
import { GroupCourse } from './pages/groups/group-courses/group-course.component';
import { CourseCreate } from './pages/courses/course-create/course-create.component';
import { AccountInfo } from './pages/account/account.info.component';
import { CourseGroups } from './pages/courses/course-groups/course-group.component';
import { ScheduleList } from './pages/schedules/schedule-list/schedule-list.component';
import { ScheduleCreate } from './pages/schedules/schedule-create/schedule-create.component';

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

      { path: 'courses', component: CourseList },
      { path: 'courses/create', component: CourseCreate },
      { path: 'courses/edit/:id', component: CourseEdit },
      { path: 'courses/:id/groups', component: CourseGroups },
      { path: 'courses/:id', component: CourseDetailed },
      { path: 'courses/:id/topics', component: CourseTopics },

      { path: 'groups', component: GroupList },
      { path: 'groups/create', component: GroupCreate },
      { path: 'groups/:id', component: GroupDetailed },
      { path: 'groups/edit/:id', component: GroupEdit },
      { path: 'groups/:id/courses', component: GroupCourse },
      { path: 'profile', component: AccountInfo },

      { path: 'schedules', component: ScheduleList },
      { path: 'schedules/create', component: ScheduleCreate },
    ]
  },
  { path: '**', redirectTo: '/login' }
];
