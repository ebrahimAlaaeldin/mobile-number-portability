import { NgTemplateOutlet } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
  effect,
  inject,
  signal,
  untracked,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { PORTING_REQUEST_TIMEOUT_MS } from '../../core/config/porting.config';
import { ApiError } from '../../core/errors/api-error';
import { PortingRequestResponse } from '../../core/models/porting-request.model';
import { NotificationService } from '../../core/services/notification.service';
import { OperatorService } from '../../core/services/operator.service';
import { PortingRequestService } from '../../core/services/porting-request.service';
import { RealtimeService } from '../../core/services/realtime.service';
import { Docket } from '../../shared/docket/docket';
import { Icon } from '../../shared/icon/icon';
import { StatusGlyph } from '../../shared/status-glyph/status-glyph';

// Realtime push (RealtimeService) handles the instant case; this is just the
// safety net for a missed/reconnecting socket, so it can be slow.
const POLL_INTERVAL_MS = 30_000;
const TICK_INTERVAL_MS = 1_000;
const URGENT_THRESHOLD_MS = 30_000;

@Component({
  selector: 'app-requests-page',
  imports: [RouterLink, StatusGlyph, Docket, Icon, NgTemplateOutlet],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './requests-page.html',
  styleUrl: './requests-page.scss',
})
export class RequestsPage implements OnInit, OnDestroy {
  private readonly portingRequestService = inject(PortingRequestService);
  private readonly notificationService = inject(NotificationService);
  private readonly realtimeService = inject(RealtimeService);
  private readonly cdr = inject(ChangeDetectorRef);
  protected readonly operatorService = inject(OperatorService);

  protected readonly requests = signal<PortingRequestResponse[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly expandedId = signal<number | null>(null);
  protected readonly actingIds = signal<ReadonlySet<number>>(new Set());

  // Pagination — 0-indexed internally, shown 1-indexed in the template.
  protected readonly page = signal(0);
  protected readonly totalPages = signal(0);
  protected readonly totalElements = signal(0);

  // Drives the live per-row countdown for PENDING requests; ticks independently
  // of the data poll so the clock doesn't visibly stall between refreshes.
  protected readonly now = signal(Date.now());

  private pollHandle?: ReturnType<typeof setInterval>;
  private tickHandle?: ReturnType<typeof setInterval>;
  private realtimeSubscription?: Subscription;

  constructor() {
    // Reloads whenever the acting operator changes — including the very first
    // run, which covers the initial load, so ngOnInit doesn't also call load().
    // untracked() around the body means page/loading/etc. writes and reads in
    // here never turn into *more* triggers for this same effect.
    effect(() => {
      this.operatorService.active();
      untracked(() => {
        this.page.set(0);
        this.expandedId.set(null);
        this.load();
      });
    });
  }

  ngOnInit(): void {
    // Safety-net poll — see POLL_INTERVAL_MS comment above.
    this.pollHandle = setInterval(() => this.load(true), POLL_INTERVAL_MS);

    // The countdown clock. markForCheck() alongside the signal write is
    // belt-and-suspenders: OnPush + signals should re-render this on its own,
    // but a ticking label is cheap enough that it's not worth leaving to chance.
    this.tickHandle = setInterval(() => {
      this.now.set(Date.now());
      this.cdr.markForCheck();
    }, TICK_INTERVAL_MS);

    // Instant refresh whenever *any* request changes anywhere — donor/recipient
    // pairs this operator isn't part of are still filtered server-side on reload,
    // so it's safe to just always re-fetch the current page rather than try to
    // merge the pushed payload in by hand.
    this.realtimeSubscription = this.realtimeService.changes$.subscribe(() => this.load(true));
  }

  ngOnDestroy(): void {
    clearInterval(this.pollHandle);
    clearInterval(this.tickHandle);
    this.realtimeSubscription?.unsubscribe();
  }

  load(silent = false): void {
    if (!silent) {
      this.loading.set(true);
    }
    this.portingRequestService.list(this.page()).subscribe({
      next: (result) => {
        this.requests.set(result.content);
        this.totalPages.set(result.totalPages);
        this.totalElements.set(result.totalElements);
        this.loading.set(false);
        this.errorMessage.set(null);
      },
      error: (err: ApiError) => {
        this.loading.set(false);
        this.errorMessage.set(err.message);
      },
    });
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages() || page === this.page()) {
      return;
    }
    this.page.set(page);
    this.expandedId.set(null);
    this.load();
  }

  toggleExpand(id: number): void {
    this.expandedId.set(this.expandedId() === id ? null : id);
  }

  canDecide(request: PortingRequestResponse): boolean {
    return request.status === 'PENDING' && request.donorOperator === this.operatorService.active().name;
  }

  isActing(id: number): boolean {
    return this.actingIds().has(id);
  }

  accept(request: PortingRequestResponse): void {
    this.decide(request, this.portingRequestService.accept(request.id), 'accepted');
  }

  reject(request: PortingRequestResponse): void {
    this.decide(request, this.portingRequestService.reject(request.id), 'rejected');
  }

  formatTime(iso: string): string {
    return new Date(parseServerTime(iso)).toLocaleString(undefined, {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  /** Milliseconds left before the background job auto-cancels this PENDING request. */
  remainingMs(request: PortingRequestResponse): number {
    // Source of truth is the server-computed deadline — no client-side timeout
    // mirroring, so a backend timeout change can't desync this clock.
    const expires = parseServerTime(request.expiresAt);
    if (Number.isFinite(expires)) {
      return Math.max(0, expires - this.now());
    }
    // Fallback for stale payloads without expiresAt (e.g. in-flight WS push
    // from before the backend upgrade): derive from createdAt + timeout.
    const created = parseServerTime(request.createdAt);
    if (!Number.isFinite(created)) {
      return 0;
    }
    const elapsed = this.now() - created;
    return Math.max(0, PORTING_REQUEST_TIMEOUT_MS - elapsed);
  }

  formatCountdown(ms: number): string {
    if (!Number.isFinite(ms) || ms <= 0) {
      return '0:00';
    }
    const totalSeconds = Math.ceil(ms / 1000);
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  }

  isUrgent(request: PortingRequestResponse): boolean {
    return this.remainingMs(request) <= URGENT_THRESHOLD_MS;
  }

  private decide(request: PortingRequestResponse, result$: Observable<PortingRequestResponse>, verb: string): void {
    this.setActing(request.id, true);
    result$.subscribe({
      next: (updated) => {
        this.requests.update((list) => list.map((r) => (r.id === updated.id ? updated : r)));
        this.setActing(request.id, false);
        this.notificationService.success(`${request.phoneNumber} ${verb}.`);
      },
      error: (err: ApiError) => {
        this.setActing(request.id, false);
        this.notificationService.error(err.message);
      },
    });
  }

  private setActing(id: number, value: boolean): void {
    this.actingIds.update((set) => {
      const next = new Set(set);
      if (value) {
        next.add(id);
      } else {
        next.delete(id);
      }
      return next;
    });
  }
}

/**
 * Parses a backend timestamp into epoch millis.
 *
 * The backend sends UTC ISO-8601 with an explicit zone ("...Z"), which
 * `new Date()` parses unambiguously in any client timezone. A zone-less value
 * ("2026-09-03T18:54:00", LocalDateTime-style from older payloads) would be
 * read as the *client's* local time — shifting the result by the
 * server↔client offset (hours) — so it is assumed to be server UTC and 'Z' is
 * appended before parsing. Returns NaN for missing/unparseable input.
 */
function parseServerTime(value: string | null | undefined): number {
  if (!value) {
    return NaN;
  }
  const trimmed = value.trim();
  if (!trimmed) {
    return NaN;
  }
  const hasZone = /(Z|[+-]\d{2}:?\d{2})$/i.test(trimmed);
  return new Date(hasZone ? trimmed : `${trimmed}Z`).getTime();
}
