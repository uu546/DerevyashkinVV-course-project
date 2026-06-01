import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { StorageService } from '../services/storage.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard {
  private storage = inject(StorageService);
  private router = inject(Router);

  canActivate(): boolean {
    if (this.storage.getToken()) {
      return true;
    }
    this.router.navigate(['/login']);
    return false;
  }
}
