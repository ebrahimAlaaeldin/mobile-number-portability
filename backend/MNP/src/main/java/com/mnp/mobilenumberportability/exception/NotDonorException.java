package com.mnp.mobilenumberportability.exception;

/** Only the donor operator on a request may accept or reject it. */
public class NotDonorException extends RuntimeException {

    public NotDonorException(Long requestId) {
        super("Only the donor operator may accept or reject porting request " + requestId);
    }
}
