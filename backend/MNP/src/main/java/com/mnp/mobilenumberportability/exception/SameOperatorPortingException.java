package com.mnp.mobilenumberportability.exception;

/** The requesting (recipient) operator already owns the number it's trying to port in. */
public class SameOperatorPortingException extends RuntimeException {

    public SameOperatorPortingException(String phoneNumber) {
        super("Phone number " + phoneNumber + " already belongs to the requesting operator");
    }
}
