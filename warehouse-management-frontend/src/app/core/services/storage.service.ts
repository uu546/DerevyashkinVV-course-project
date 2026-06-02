import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class StorageService {
  private readonly TOKEN_KEY = 'access_token';
  private readonly USER_KEY = 'user_data';

  private isAuthenticatedSignal = signal<boolean>(this.hasToken());

  isAuthenticated = this.isAuthenticatedSignal.asReadonly();

  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    this.isAuthenticatedSignal.set(true);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  clear(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.isAuthenticatedSignal.set(false);
  }

  setUser(user: any): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  getUser(): any {
    const user = localStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  getUserRole(): string | null {
    const user = this.getUser();
    return user ? user.role : null;
  }

  private hasToken(): boolean {
    return !!this.getToken();
  }
}
