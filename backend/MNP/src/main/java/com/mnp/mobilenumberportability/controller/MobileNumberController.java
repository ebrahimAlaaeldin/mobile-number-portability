package com.mnp.mobilenumberportability.controller;

import com.mnp.mobilenumberportability.dto.MobileNumberResponse;
import com.mnp.mobilenumberportability.entity.Operator;
import com.mnp.mobilenumberportability.security.CurrentOperator;
import com.mnp.mobilenumberportability.service.MobileNumberService;
import com.mnp.mobilenumberportability.support.PhoneNumbers;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mobile-numbers")
@RequiredArgsConstructor
@Validated // enables @Pattern checking on the path variable below
public class MobileNumberController {

    private final MobileNumberService mobileNumberService;

    // `operator` is unused beyond proving the caller is a known operator agent — the
    // status of a number doesn't depend on who's asking, but every operator-facing
    // endpoint should still require the mocked auth header.
    @GetMapping("/{phoneNumber}")
    public MobileNumberResponse status(
            @PathVariable
            @Pattern(regexp = PhoneNumbers.PATTERN, message = "Phone number must be a valid Egyptian mobile number")
            String phoneNumber,
            @CurrentOperator Operator operator) {
        return mobileNumberService.getStatus(phoneNumber);
    }
}
