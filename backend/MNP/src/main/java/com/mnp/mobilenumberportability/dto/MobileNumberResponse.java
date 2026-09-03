package com.mnp.mobilenumberportability.dto;

import java.time.LocalDate;


public record MobileNumberResponse(
        String phoneNumber,
        String currentOperator,
        boolean ported,
        LocalDate portedAt
) {
}
