package com.mnp.mobilenumberportability.exception;

/** The number already has another porting request in flight. */
public class DuplicatePendingRequestException extends RuntimeException {

    public DuplicatePendingRequestException(String phoneNumber) {
        super("Phone number " + phoneNumber + " already has a pending porting request");
    }
}
