import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { ApiService } from '../../../core/services/api.service';
import { InventorySummaryResponse } from '../models/inventory-summary-response';

@Component({
  selector: 'app-summary',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './summary.component.html',
  styleUrls: ['./summary.component.css'],
})
export class SummaryComponent implements OnInit {
  private api = inject(ApiService);

  inventoryData = signal<InventorySummaryResponse | null>(null);
  isLoading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.loadInventory();
  }

  loadInventory(): void {
    this.isLoading.set(true);
    this.api.get<InventorySummaryResponse>('/inventory/summary').subscribe({
      next: (data) => {
        this.inventoryData.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.error.set('Ошибка загрузки данных');
        this.isLoading.set(false);
      },
    });
  }
}
