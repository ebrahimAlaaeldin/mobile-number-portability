import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from '../config/api.config';
import { MobileNumberResponse } from '../models/mobile-number.model';

@Injectable({ providedIn: 'root' })
export class MobileNumberService {
  private readonly http = inject(HttpClient);
  private readonly base = `${API_BASE}/mobile-numbers`;

  getStatus(phoneNumber: string): Observable<MobileNumberResponse> {
    return this.http.get<MobileNumberResponse>(`${this.base}/${phoneNumber}`);
  }
}
