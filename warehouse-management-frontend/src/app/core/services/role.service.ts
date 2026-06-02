import { inject, Injectable } from '@angular/core';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root',
})
export class RoleService {
  private storage = inject(StorageService);

  isManager(): boolean {
    const user = this.storage.getUser();
    return user?.role === 'MANAGER';
  }

  isUser(): boolean {
    const user = this.storage.getUser();
    return user?.role === 'USER';
  }

  hasWriteAccess(): boolean {
    return this.isManager();
  }

  hasReadAccess(): boolean {
    return this.isManager() || this.isUser();
  }

  getCurrentRole(): string | null {
    const user = this.storage.getUser();
    return user?.role || null;
  }
}
