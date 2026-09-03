import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { PortingRequestStatus } from '../../core/models/porting-request.model';

/**
 * Status never reads as a colored pill: pending is an unlit ring, accepted is
 * a struck-forward glow, rejected is dark with a cross, canceled is an
 * embered dash — shape carries the meaning as much as color does.
 */
@Component({
  selector: 'app-status-glyph',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <span class="glyph" [class]="'is-' + status().toLowerCase()">
      <svg viewBox="0 0 12 12" width="10" height="10" aria-hidden="true">
        @switch (status()) {
          @case ('PENDING') {
            <circle cx="6" cy="6" r="4" fill="none" stroke="currentColor" stroke-width="1.6" />
          }
          @case ('ACCEPTED') {
            <circle cx="6" cy="6" r="5" fill="currentColor" />
          }
          @case ('REJECTED') {
            <circle cx="6" cy="6" r="5" fill="currentColor" />
            <path d="M4.2 4.2 7.8 7.8M7.8 4.2 4.2 7.8" stroke="#0a0e13" stroke-width="1.2" stroke-linecap="round" />
          }
          @case ('CANCELED') {
            <circle cx="6" cy="6" r="5" fill="currentColor" />
            <path d="M3.8 6H8.2" stroke="#0a0e13" stroke-width="1.3" stroke-linecap="round" />
          }
        }
      </svg>
      <span class="label">{{ label() }}</span>
    </span>
  `,
  styles: `
    .glyph {
      display: inline-flex;
      align-items: center;
      gap: 0.4rem;
      font-family: var(--font-mono);
      font-size: 0.78rem;
      letter-spacing: 0.03em;
      text-transform: uppercase;
      white-space: nowrap;
    }
    .is-pending {
      color: var(--status-pending);
    }
    .is-accepted {
      color: var(--status-accepted);
      svg {
        filter: drop-shadow(0 0 4px var(--status-accepted-glow));
      }
    }
    .is-rejected {
      color: var(--status-rejected);
    }
    .is-canceled {
      color: var(--status-canceled);
    }
  `,
})
export class StatusGlyph {
  readonly status = input.required<PortingRequestStatus>();
  readonly label = computed(() => {
    const s = this.status();
    return s.charAt(0) + s.slice(1).toLowerCase();
  });
}
