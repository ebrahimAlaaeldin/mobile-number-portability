/** RFC 9457 shape every backend error comes back as (see GlobalExceptionHandler). */
export interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
}
