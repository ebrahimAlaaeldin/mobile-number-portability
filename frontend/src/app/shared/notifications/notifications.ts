import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-notifications',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="stack" aria-live="polite">
      @for (notice of notificationService.notices(); track notice.id) {
        <div class="notice" [class]="'notice--' + notice.kind" (click)="notificationService.dismiss(notice.id)">
          {{ notice.message }}
        </div>
      }
    </div>
  `,
  styles: `
    .stack {
      position: fixed;
      top: var(--space-4);
      right: var(--space-4);
      z-index: 100;
      display: flex;
      flex-direction: column;
      gap: var(--space-2);
      max-width: 22rem;
    }
    .notice {
      cursor: pointer;
      padding: var(--space-3) var(--space-4);
      border-radius: var(--radius-md);
      font-size: 0.85rem;
      background: var(--bg-raised);
      border: 1px solid var(--hairline-strong);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
      animation: rise 0.15s ease-out;
    }
    .notice--success {
      color: var(--accent-strong);
      border-color: rgba(255, 154, 68, 0.35);
    }
    .notice--error {
      color: var(--status-rejected);
      border-color: rgba(209, 102, 95, 0.35);
    }
    @keyframes rise {
      from {
        opacity: 0;
        transform: translateY(-4px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
  `,
})
export class Notifications {
  protected readonly notificationService = inject(NotificationService);
}
