/** The three operators the backend seeds, keyed by their `organization` header value. */
export interface OperatorOption {
  readonly code: 'vodafone' | 'orange' | 'etisalat';
  readonly name: string;
}

export const OPERATORS: readonly OperatorOption[] = [
  { code: 'vodafone', name: 'Vodafone' },
  { code: 'orange', name: 'Orange' },
  { code: 'etisalat', name: 'Etisalat' },
];
