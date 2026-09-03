package com.mnp.mobilenumberportability.service;

import com.mnp.mobilenumberportability.dto.MobileNumberResponse;
import com.mnp.mobilenumberportability.entity.MobileNumber;
import com.mnp.mobilenumberportability.entity.Operator;
import com.mnp.mobilenumberportability.exception.UnrecognizedPhoneNumberException;
import com.mnp.mobilenumberportability.repository.MobileNumberRepository;
import com.mnp.mobilenumberportability.repository.OperatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MobileNumberService {

    private final MobileNumberRepository mobileNumberRepository;
    private final OperatorRepository operatorRepository;

    /** Read-only status lookup; never provisions a row just because someone asked. */
    @Transactional(readOnly = true)
    public MobileNumberResponse getStatus(String phoneNumber) {
        return mobileNumberRepository.findByPhoneNumber(phoneNumber)
                .map(this::toResponse)
                .orElseGet(() -> new MobileNumberResponse(
                        phoneNumber, resolveOriginalOperator(phoneNumber).getName(), false, null));
    }

    /**
     * Returns the persisted {@link MobileNumber} for a phone number, creating it on
     * first use. Every number starts out owned by whichever operator its range belongs
     * to; the row only needs to exist once a porting request has to reference it.
     */
    @Transactional
    public MobileNumber resolveOrProvision(String phoneNumber) {
        return mobileNumberRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> mobileNumberRepository.save(
                        MobileNumber.provision(phoneNumber, resolveOriginalOperator(phoneNumber))));
    }

    private Operator resolveOriginalOperator(String phoneNumber) {
        long numericNumber = Long.parseLong(phoneNumber);
        return operatorRepository.findByRange(numericNumber)
                .orElseThrow(() -> new UnrecognizedPhoneNumberException(phoneNumber));
    }

    private MobileNumberResponse toResponse(MobileNumber number) {
        return new MobileNumberResponse(
                number.getPhoneNumber(),
                number.getCurrentOperator().getName(),
                number.getOperatorSince() != null,
                number.getOperatorSince()
        );
    }
}
