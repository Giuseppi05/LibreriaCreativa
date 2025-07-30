import { SidebarAdmin } from './components/globalComponents/sidebar-admin/sidebar-admin';
import { Loading } from './components/globalComponents/loading/loading';
import { Navbar } from './components/globalComponents/navbar/navbar';
import { Footer } from './components/globalComponents/footer/footer';
import { AuthService } from './services/auth-service';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet, 
    Navbar, 
    Footer,
    Loading,
    SidebarAdmin,
    CommonModule
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})

export class App {
  protected title = 'LibreriaCreativa_Frontend';
  isAdmin = false

  private subscriptions = new Subscription()

  constructor(
    private authService: AuthService
  ){}

  ngOnInit(){
    this.subscriptions.add(
      this.authService.isAdmin$.subscribe((value) => (this.isAdmin = value))
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
