import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthRequest } from '../../models/types';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  authRequest: AuthRequest = { username: '', password: '' };
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.authRequest).subscribe({
      next: (response) => {
        // Token is already saved by the service's tap() operator
        this.isLoading = false;
        this.router.navigate(['/students']); // Redirect to dashboard
      },
      error: (err) => {
        this.isLoading = false;
        console.error(err);
        this.errorMessage = 'Invalid username or password';
      }
    });
  }
}
