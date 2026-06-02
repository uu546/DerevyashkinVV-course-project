import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { RoleService } from '../../core/services/role.service';

@Component({
  selector: 'app-header',
  imports: [CommonModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  roleService = inject(RoleService);
  user = this.authService.currentUser;

  logout(): void {
    this.authService.logout();
  }
}
