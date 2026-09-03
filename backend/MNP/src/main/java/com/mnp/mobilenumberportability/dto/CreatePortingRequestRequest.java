package com.mnp.mobilenumberportability.dto;

import com.mnp.mobilenumberportability.support.PhoneNumbers;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreatePortingRequestRequest(

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = PhoneNumbers.PATTERN,
                message = "Phone number must be a valid Egyptian mobile number"
        )
        String phoneNumber

) {
}