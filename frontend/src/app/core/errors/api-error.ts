import { ProblemDetail } from '../models/problem-detail.model';

/**
 * Normalized shape every failed API call throws as, whatever the backend
 * actually sent — components read `.message` and get something worth
 * showing a human, never a raw HttpErrorResponse.
 */
export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, problem: ProblemDetail | null) {
    super(problem?.detail || problem?.title || `Request failed (${status})`);
    this.status = status;
    this.name = 'ApiError';
  }
}
