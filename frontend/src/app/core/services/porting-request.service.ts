import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE } from '../config/api.config';
import { PageResponse } from '../models/page-response.model';
import { PortingRequestResponse } from '../models/porting-request.model';

@Injectable({ providedIn: 'root' })
export class PortingRequestService {
  private readonly http = inject(HttpClient);
  private readonly base = `${API_BASE}/porting-requests`;

  /** Caller acts as the Recipient — the organization header interceptor supplies who. */
  create(phoneNumber: string): Observable<PortingRequestResponse> {
    return this.http.post<PortingRequestResponse>(this.base, { phoneNumber });
  }

  /** Backend already filters to what the calling operator may see. `page` is 0-indexed. */
  list(page: number): Observable<PageResponse<PortingRequestResponse>> {
    const params = new HttpParams().set('page', page);
    return this.http.get<PageResponse<PortingRequestResponse>>(this.base, { params });
  }

  accept(id: number): Observable<PortingRequestResponse> {
    return this.http.post<PortingRequestResponse>(`${this.base}/${id}/accept`, {});
  }

  reject(id: number): Observable<PortingRequestResponse> {
    return this.http.post<PortingRequestResponse>(`${this.base}/${id}/reject`, {});
  }
}
