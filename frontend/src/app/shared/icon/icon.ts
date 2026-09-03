import { ChangeDetectionStrategy, Component, input } from '@angular/core';

export type IconName = 'check' | 'x' | 'search' | 'plus' | 'chevron-down' | 'refresh' | 'arrow-right';

/**
 * Small authored icon set, one consistent stroke weight — deliberately not a
 * full icon-library dependency, and never emoji standing in for one.
 */
@Component({
  selector: 'app-icon',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <svg viewBox="0 0 20 20" width="1em" height="1em" fill="none" stroke="currentColor"
         stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
      @switch (name()) {
        @case ('check') {
          <path d="M4 10.5 8 14.5 16 5.5" />
        }
        @case ('x') {
          <path d="M5 5 15 15M15 5 5 15" />
        }
        @case ('search') {
          <circle cx="8.5" cy="8.5" r="5.2" />
          <path d="M16 16 12.6 12.6" />
        }
        @case ('plus') {
          <path d="M10 4v12M4 10h12" />
        }
        @case ('chevron-down') {
          <path d="M5 7.5 10 12.5 15 7.5" />
        }
        @case ('refresh') {
          <path d="M15 4v4h-4" />
          <path d="M4.6 11a5.5 5.5 0 0 1 9.5-4.3L15 8" />
          <path d="M5 16v-4h4" />
          <path d="M15.4 9a5.5 5.5 0 0 1-9.5 4.3L5 12" />
        }
        @case ('arrow-right') {
          <path d="M4 10h11M11 5l5 5-5 5" />
        }
      }
    </svg>
  `,
  styles: `
    :host {
      display: inline-flex;
      font-size: 1rem;
      line-height: 0;
    }
  `,
})
export class Icon {
  readonly name = input.required<IconName>();
}
