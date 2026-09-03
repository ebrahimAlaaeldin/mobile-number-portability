package com.mnp.mobilenumberportability.exception;

/** No porting request exists with the given id. */
public class PortingRequestNotFoundException extends RuntimeException {

    public PortingRequestNotFoundException(Long id) {
        super("Porting request " + id + " was not found");
    }
}
