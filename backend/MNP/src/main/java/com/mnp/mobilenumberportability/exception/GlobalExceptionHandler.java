package com.mnp.mobilenumberportability.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Translates every exception the app throws into an RFC 9457 ProblemDetail body, so
 * clients get one consistent error shape instead of raw stack traces or ad-hoc JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return problem(HttpStatus.BAD_REQUEST, "Validation failed", detail);
    }

    // Validation on plain @PathVariable/@RequestParam args (e.g. MobileNumberController's
    // @Pattern-checked phone number) surfaces here rather than through the binding result
    // that MethodArgumentNotValidException wraps.
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        String detail = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        return problem(HttpStatus.BAD_REQUEST, "Validation failed", detail);
    }

    @ExceptionHandler(UnknownOperatorException.class)
    public ProblemDetail handleUnknownOperator(UnknownOperatorException ex) {
        return problem(HttpStatus.UNAUTHORIZED, "Unknown operator", ex.getMessage());
    }

    @ExceptionHandler(PortingRequestNotFoundException.class)
    public ProblemDetail handleNotFound(PortingRequestNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Porting request not found", ex.getMessage());
    }

    @ExceptionHandler(UnrecognizedPhoneNumberException.class)
    public ProblemDetail handleUnrecognizedNumber(UnrecognizedPhoneNumberException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Unrecognized phone number", ex.getMessage());
    }

    @ExceptionHandler(NotDonorException.class)
    public ProblemDetail handleNotDonor(NotDonorException ex) {
        return problem(HttpStatus.FORBIDDEN, "Not the donor operator", ex.getMessage());
    }

    @ExceptionHandler(DuplicatePendingRequestException.class)
    public ProblemDetail handleDuplicate(DuplicatePendingRequestException ex) {
        return problem(HttpStatus.CONFLICT, "Duplicate pending request", ex.getMessage());
    }

    @ExceptionHandler(SameOperatorPortingException.class)
    public ProblemDetail handleSameOperator(SameOperatorPortingException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid porting request", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        return problem(HttpStatus.CONFLICT, "Invalid state transition", ex.getMessage());
    }

    // Safety net: if two requests for the same number race past the application-level
    // duplicate check, the DB's partial unique index (see db/init.sql) rejects the second
    // insert and it surfaces here instead of as a raw 500.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleIntegrityViolation(DataIntegrityViolationException ex) {
        return problem(HttpStatus.CONFLICT, "Conflicting request",
                "This phone number already has a pending porting request");
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle(title);
        problem.setDetail(detail);
        return problem;
    }
}
