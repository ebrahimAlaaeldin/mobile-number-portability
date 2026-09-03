package com.mnp.mobilenumberportability.exception;

/** The `organization` header is missing, blank, or doesn't match a known operator. */
public class UnknownOperatorException extends RuntimeException {

    public UnknownOperatorException(String message) {
        super(message);
    }
}
