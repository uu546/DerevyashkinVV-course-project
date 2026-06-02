import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { StorageService } from '../services/storage.service';
import { RoleService } from '../services/role.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard {
  private storage = inject(StorageService);
  private roleService = inject(RoleService);
  private router = inject(Router);

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const token = this.storage.getToken();

    if (!token) {
      this.router.navigate(['/login']);
      return false;
    }

    const requiredRole = route.data['role'];

    if (requiredRole === 'MANAGER' && this.roleService.isManager() === false) {
      this.router.navigate(['/inventory/summary']);
      return false;
    }

    return true;
  }
}
