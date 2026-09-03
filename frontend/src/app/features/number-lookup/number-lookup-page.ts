import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ApiError } from '../../core/errors/api-error';
import { MobileNumberResponse } from '../../core/models/mobile-number.model';
import { MobileNumberService } from '../../core/services/mobile-number.service';
import { Icon } from '../../shared/icon/icon';

const PHONE_PATTERN = /^01[012]\d{8}$/;

@Component({
  selector: 'app-number-lookup-page',
  imports: [Icon],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './number-lookup-page.html',
  styleUrl: './number-lookup-page.scss',
})
export class NumberLookupPage {
  private readonly mobileNumberService = inject(MobileNumberService);

  protected readonly phoneNumber = signal('');
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly result = signal<MobileNumberResponse | null>(null);

  protected readonly isValid = computed(() => PHONE_PATTERN.test(this.phoneNumber()));

  onInput(value: string): void {
    this.phoneNumber.set(value.trim());
    this.result.set(null);
    this.errorMessage.set(null);
  }

  check(): void {
    if (!this.isValid() || this.loading()) {
      return;
    }
    this.loading.set(true);
    this.errorMessage.set(null);
    this.result.set(null);

    this.mobileNumberService.getStatus(this.phoneNumber()).subscribe({
      next: (response) => {
        this.result.set(response);
        this.loading.set(false);
      },
      error: (err: ApiError) => {
        this.errorMessage.set(err.message);
        this.loading.set(false);
      },
    });
  }
}
