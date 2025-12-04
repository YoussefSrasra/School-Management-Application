import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { StudentListComponent } from './components/student-list/student-list.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'students', component: StudentListComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' }
];
