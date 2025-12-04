import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { StudentListComponent } from './components/student-list/student-list.component';
import {authGuard} from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'students', component: StudentListComponent , canActivate: [authGuard]},
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
