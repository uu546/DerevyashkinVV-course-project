import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/inventory/summary', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then((m) => m.RegisterComponent),
  },
  {
    path: 'inventory',
    canActivate: [AuthGuard],
    children: [
      {
        path: 'summary',
        loadComponent: () =>
          import('./features/inventory/summary/summary.component').then((m) => m.SummaryComponent),
      },
    ],
  },
  {
    path: 'movements',
    canActivate: [AuthGuard],
    data: { role: 'MANAGER' },
    children: [
      {
        path: 'receipt',
        loadComponent: () =>
          import('./features/movements//receipt/receipt.component').then((m) => m.ReceiptComponent),
      },
      {
        path: 'shipment',
        loadComponent: () =>
          import('./features/movements/shipment/shipment.component').then(
            (m) => m.ShipmentComponent,
          ),
      },
      {
        path: 'transfer',
        loadComponent: () =>
          import('./features/movements/transfer/transfer.component').then(
            (m) => m.TransferComponent,
          ),
      },
    ],
  },
  { path: '**', redirectTo: '/inventory/summary' },
];
