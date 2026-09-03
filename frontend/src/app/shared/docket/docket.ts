import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { PortingRequestResponse } from '../../core/models/porting-request.model';

/**
 * Echoes the spec's own Recipient → System → Donor sequence diagram as a
 * compact three-node docket, so a request's position in that handshake is
 * always visible, not just its final status word.
 *
 * Once ACCEPTED the number has actually moved, so the docket switches to a
 * two-node Donor → Recipient view with a green directional line — the porting
 * direction, not the request direction.
 */
@Component({
  selector: 'app-docket',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './docket.html',
  styleUrl: './docket.scss',
})
export class Docket {
  readonly request = input.required<PortingRequestResponse>();

  protected readonly isAccepted = computed(() => this.request().status === 'ACCEPTED');

  // Which of the two connecting segments — and the System node itself — are
  // "live" depends on where the request currently sits in the handshake.
  protected readonly systemState = computed(() => {
    switch (this.request().status) {
      case 'CANCELED':
        return 'ember';
      case 'PENDING':
        return 'active';
      default:
        return 'settled';
    }
  });

  protected readonly donorState = computed(() => {
    switch (this.request().status) {
      case 'ACCEPTED':
        return 'accepted';
      case 'REJECTED':
        return 'rejected';
      default:
        return 'waiting';
    }
  });
}
