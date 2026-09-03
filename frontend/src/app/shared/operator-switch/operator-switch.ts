import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { OperatorService } from '../../core/services/operator.service';

/**
 * The single most important control in the app: every request the whole UI
 * makes is stamped with whoever is struck forward here. Three operators sit
 * present as ghosts; the active one strikes forward in an amber glow.
 */
@Component({
  selector: 'app-operator-switch',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './operator-switch.html',
  styleUrl: './operator-switch.scss',
})
export class OperatorSwitch {
  protected readonly operatorService = inject(OperatorService);
}
