package com.mnp.mobilenumberportability.dto;


import com.mnp.mobilenumberportability.entity.PortingRequestStatus;

import java.time.LocalDateTime;

public record PortingRequestResponse(
        Long id,
        String phoneNumber,
        String donorOperator,
        String recipientOperator,
        PortingRequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime resolvedAt
) {
}
