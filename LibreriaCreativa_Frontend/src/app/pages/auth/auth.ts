import { Register } from '../../components/authComponents/register/register';
import { AuthService } from '../../services/auth-service';
import { Login } from '../../components/authComponents/login/login';
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-auth',
  imports: [Register, Login, CommonModule],
  templateUrl: './auth.html',
  styleUrl: './auth.css',
})
export class Auth {
  isRegisterMode = false;

  private subscriptions = new Subscription();

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.subscriptions.add(
      this.authService.isAuthenticated$.subscribe(
        (value) => {if(value) this.router.navigate(['/'])}
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  toggleForm() {
    this.isRegisterMode = !this.isRegisterMode;
  }
}
