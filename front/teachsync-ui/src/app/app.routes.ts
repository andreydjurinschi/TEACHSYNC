import { Routes } from '@angular/router';

import { AuthGuard } from './core/auth/AuthGuard';
import { RoleGuard } from './core/auth/RoleGuard';
import { LayoutComponent } from './pages/layout/layout.component';
import { LoginComponent } from './pages/login/login-page';
import { ForbiddenPageComponent } from './pages/errors/forbidden-page.component';
import { NotFoundPageComponent } from './pages/errors/not-found-page.component';
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
import { ScheduleCreateComponent } from './pages/schedules/schedule-create/schedule-create.component';
import { MyCoursesComponent } from './pages/account/teacher/courses/my-courses.component';
import { TeacherScheduleComponent } from './pages/account/teacher/schedules/my-schedule.component';
import { NotificationsComponent } from './pages/notifications/notifications.component';
import { ReplacementsComponent } from './pages/replacements/replacements.component';
import { ManagerReplacementsComponent } from './pages/replacements/manager-replacements.component';
import { ClassroomListComponent } from './pages/classrooms/classroom-list.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'forbidden', component: ForbiddenPageComponent },
  { path: 'not-found', component: NotFoundPageComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: '/login', pathMatch: 'full' },
      { path: 'users', component: UserList, canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },
      { path: 'users/create', component: UserCreate, canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },
      { path: 'users/edit/:id', component: UserEdit, canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },
      { path: 'users/:id', component: UserDetailed, canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },

      { path: 'courses', component: CourseList },
      { path: 'courses/create', component: CourseCreate, canActivate: [RoleGuard], data: { roles: ['ADMIN', 'MANAGER'] } },
      { path: 'courses/edit/:id', component: CourseEdit },
      { path: 'courses/:id/groups', component: CourseGroups, canActivate: [RoleGuard], data: { roles: ['ADMIN', 'MANAGER'] } },
      { path: 'courses/:id', component: CourseDetailed },
      { path: 'courses/:id/topics', component: CourseTopics, canActivate: [RoleGuard], data: { roles: ['ADMIN', 'MANAGER'] } },

      { path: 'groups', component: GroupList, canActivate: [RoleGuard], data: { roles: ['ADMIN', 'MANAGER'] } },
      { path: 'groups/create', component: GroupCreate, canActivate: [RoleGuard], data: { roles: ['ADMIN', 'MANAGER'] } },
      { path: 'groups/:id', component: GroupDetailed, canActivate: [RoleGuard], data: { roles: ['ADMIN', 'MANAGER'] } },
      { path: 'groups/edit/:id', component: GroupEdit, canActivate: [RoleGuard], data: { roles: ['ADMIN', 'MANAGER'] } },
      { path: 'groups/:id/courses', component: GroupCourse, canActivate: [RoleGuard], data: { roles: ['ADMIN', 'MANAGER'] } },
      { path: 'profile', component: AccountInfo },
      { path: 'statistics', component: AccountInfo },

      { path: 'schedules', component: ScheduleList, canActivate: [RoleGuard], data: { roles: ['ADMIN', 'MANAGER'] } },
      { path: 'schedules/create', component: ScheduleCreateComponent, canActivate: [RoleGuard], data: { roles: ['ADMIN', 'MANAGER'] } },
      { path: 'classrooms', component: ClassroomListComponent, canActivate: [RoleGuard], data: { roles: ['ADMIN'] } },

      { path: 'profile/courses', component: MyCoursesComponent, canActivate: [RoleGuard], data: { roles: ['TEACHER'] } },
      { path: 'profile/schedules', component: TeacherScheduleComponent, canActivate: [RoleGuard], data: { roles: ['TEACHER'] } },
      { path: 'profile/replacements', component: ReplacementsComponent, canActivate: [RoleGuard], data: { roles: ['TEACHER'] } },
      { path: 'manager/replacements', component: ManagerReplacementsComponent, canActivate: [RoleGuard], data: { roles: ['MANAGER'] } },
      { path: 'notifications', component: NotificationsComponent }
    ]
  },
  { path: '**', redirectTo: '/not-found' }
];
