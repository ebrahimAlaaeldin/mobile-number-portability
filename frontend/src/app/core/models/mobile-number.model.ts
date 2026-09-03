/** Mirrors MobileNumberResponse on the backend. */
export interface MobileNumberResponse {
  phoneNumber: string;
  currentOperator: string;
  ported: boolean;
  portedAt: string | null;
}
