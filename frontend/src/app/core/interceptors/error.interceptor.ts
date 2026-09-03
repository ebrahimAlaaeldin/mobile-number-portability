import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { ApiError } from '../errors/api-error';

/**
 * Converts every failed request into an ApiError carrying the backend's own
 * ProblemDetail message, so a page never has to reach into HttpErrorResponse
 * internals to say something specific to the user.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) =>
  next(req).pipe(
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse) {
        const problem = typeof err.error === 'object' && err.error !== null ? err.error : null;
        return throwError(() => new ApiError(err.status, problem));
      }
      return throwError(() => err);
    })
  );
