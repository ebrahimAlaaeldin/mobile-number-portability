import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiError } from '../../core/errors/api-error';
import { PortingRequestResponse } from '../../core/models/porting-request.model';
import { OperatorService } from '../../core/services/operator.service';
import { PortingRequestService } from '../../core/services/porting-request.service';
import { Icon } from '../../shared/icon/icon';
import { StatusGlyph } from '../../shared/status-glyph/status-glyph';

const PHONE_PATTERN = /^01[012]\d{8}$/;

@Component({
  selector: 'app-new-request-page',
  imports: [RouterLink, Icon, StatusGlyph],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './new-request-page.html',
  styleUrl: './new-request-page.scss',
})
export class NewRequestPage {
  private readonly portingRequestService = inject(PortingRequestService);
  protected readonly operatorService = inject(OperatorService);

  protected readonly phoneNumber = signal('');
  protected readonly touched = signal(false);
  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly created = signal<PortingRequestResponse | null>(null);

  protected readonly isValid = computed(() => PHONE_PATTERN.test(this.phoneNumber()));
  protected readonly showValidationHint = computed(() => this.touched() && !this.isValid() && this.phoneNumber().length > 0);

  onInput(value: string): void {
    this.phoneNumber.set(value.trim());
    this.created.set(null);
  }

  submit(): void {
    this.touched.set(true);
    if (!this.isValid() || this.submitting()) {
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set(null);
    this.created.set(null);

    this.portingRequestService.create(this.phoneNumber()).subscribe({
      next: (response) => {
        this.created.set(response);
        this.phoneNumber.set('');
        this.touched.set(false);
        this.submitting.set(false);
      },
      error: (err: ApiError) => {
        this.errorMessage.set(err.message);
        this.submitting.set(false);
      },
    });
  }
}
