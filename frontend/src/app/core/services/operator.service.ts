import { Injectable, signal } from '@angular/core';
import { OPERATORS, OperatorOption } from '../models/operator.model';

const STORAGE_KEY = 'mnp.active-operator';

/**
 * Holds "who the agent is acting as" — the entire mocked-auth model rests on
 * this one client-side choice, since every request just carries it as the
 * `organization` header. Persisted to localStorage only so a page refresh
 * mid-demo doesn't silently reset who you're acting as.
 */
@Injectable({ providedIn: 'root' })
export class OperatorService {
  readonly operators = OPERATORS;
  readonly active = signal<OperatorOption>(this.restore());

  setActive(code: OperatorOption['code']): void {
    const operator = this.operators.find((o) => o.code === code);
    if (!operator) {
      return;
    }
    this.active.set(operator);
    localStorage.setItem(STORAGE_KEY, code);
  }

  private restore(): OperatorOption {
    const saved = localStorage.getItem(STORAGE_KEY);
    return this.operators.find((o) => o.code === saved) ?? this.operators[0];
  }
}
