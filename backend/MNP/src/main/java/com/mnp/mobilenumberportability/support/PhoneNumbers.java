package com.mnp.mobilenumberportability.support;

/**
 * Single source of truth for what a valid Egyptian mobile number looks like, so the
 * request-body validation and the path-variable validation can't drift apart.
 */
public final class PhoneNumbers {

    /** 01[0/1/2] + 8 digits = 11 digits total, spanning all three operators' ranges. */
    public static final String PATTERN = "01[012]\\d{8}";

    private PhoneNumbers() {
    }
}
