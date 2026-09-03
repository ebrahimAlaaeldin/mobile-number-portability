import { Injectable, signal } from '@angular/core';

export interface Notice {
  id: number;
  kind: 'success' | 'error';
  message: string;
}

const AUTO_DISMISS_MS = 5000;

/** Small toast stack for secondary-action feedback (accept/reject, etc). */
@Injectable({ providedIn: 'root' })
export class NotificationService {
  readonly notices = signal<Notice[]>([]);
  private nextId = 1;

  success(message: string): void {
    this.push('success', message);
  }

  error(message: string): void {
    this.push('error', message);
  }

  dismiss(id: number): void {
    this.notices.update((list) => list.filter((n) => n.id !== id));
  }

  private push(kind: Notice['kind'], message: string): void {
    const id = this.nextId++;
    this.notices.update((list) => [...list, { id, kind, message }]);
    setTimeout(() => this.dismiss(id), AUTO_DISMISS_MS);
  }
}
