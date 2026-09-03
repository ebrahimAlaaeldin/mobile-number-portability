import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { OperatorService } from '../services/operator.service';

/** Stands in for real auth: every request identifies its caller by this header alone. */
export const organizationHeaderInterceptor: HttpInterceptorFn = (req, next) => {
  const operator = inject(OperatorService).active();
  return next(req.clone({ setHeaders: { organization: operator.code } }));
};
