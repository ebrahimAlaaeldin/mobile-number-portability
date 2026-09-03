package com.mnp.mobilenumberportability.exception;

/** The phone number doesn't fall inside any operator's allocated range. */
public class UnrecognizedPhoneNumberException extends RuntimeException {

    public UnrecognizedPhoneNumberException(String phoneNumber) {
        super("No operator owns the range for phone number " + phoneNumber);
    }
}
