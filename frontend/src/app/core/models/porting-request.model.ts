export type PortingRequestStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELED';

/** Mirrors PortingRequestResponse on the backend. */
export interface PortingRequestResponse {
  id: number;
  phoneNumber: string;
  donorOperator: string;
  recipientOperator: string;
  status: PortingRequestStatus;
  /** UTC ISO-8601 instant with trailing 'Z' (e.g. "2026-09-03T18:54:00Z"). */
  createdAt: string;
  updatedAt: string;
  resolvedAt: string | null;
  /**
   * Absolute PENDING auto-cancel deadline (createdAt + server timeout).
   * Optional only for tolerance of stale cached payloads — the backend always sends it.
   */
  expiresAt?: string | null;
}
