import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { StorageService } from './storage.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = inject(ApiService);
  private storage = inject(StorageService);
  private router = inject(Router);

  private currentUserSignal = signal<any>(this.storage.getUser());
  currentUser = this.currentUserSignal.asReadonly();

  login(email: string, password: string): Observable<any> {
    return this.api.post('/auth/login', { email, password });
  }

  register(email: string, password: string, fullName: string): Observable<any> {
    return this.api.post('/auth/register', { email, password, fullName });
  }

  handleAuthResponse(response: any): void {
    this.storage.setToken(response.accessToken);
    this.storage.setUser({
      email: response.email,
      fullName: response.fullName,
      role: response.role
    });
    this.currentUserSignal.set(this.storage.getUser());
  }

  logout(): void {
    this.storage.clear();
    this.currentUserSignal.set(null);
    this.router.navigate(['/login']);
  }
}