import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HostListener } from '@angular/core';

@Component({
  selector: 'app-sidebar-admin',
  imports: [RouterLink, CommonModule],
  templateUrl: './sidebar-admin.html',
  styleUrl: './sidebar-admin.css'
})

export class SidebarAdmin {
  sidebarVisible = false;
  isDesktop = window.innerWidth >= 992;

  ngOnInit() {
    this.onResize(); // inicial
  }

  @HostListener('window:resize')
  onResize() {
    this.isDesktop = window.innerWidth >= 992;
    if (this.isDesktop) {
      this.sidebarVisible = false;
    }
  }

  toggleSidebar() {
    this.sidebarVisible = !this.sidebarVisible;
  }

  closeSidebar() {
    if (!this.isDesktop) {
      this.sidebarVisible = false;
    }
  }
}