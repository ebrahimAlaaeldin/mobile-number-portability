package com.mnp.mobilenumberportability.mapper;


import com.mnp.mobilenumberportability.config.PortingProperties;
import com.mnp.mobilenumberportability.dto.PortingRequestResponse;
import com.mnp.mobilenumberportability.entity.PortingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class PortingRequestMapper {

    private final PortingProperties portingProperties;

    public PortingRequestResponse toResponse(PortingRequest request) {
        Instant createdAt = toInstant(request.getCreatedAt());
        Instant expiresAt = createdAt != null ? createdAt.plus(portingProperties.requestTimeout()) : null;
        return new PortingRequestResponse(
                request.getId(),
                request.getMobileNumber().getPhoneNumber(),
                request.getDonorOperator().getName(),
                request.getRecipientOperator().getName(),
                request.getStatus(),
                createdAt,
                toInstant(request.getUpdatedAt()),
                toInstant(request.getResolvedAt()),
                expiresAt
        );
    }

    // Entities persist wall-clock LocalDateTime in the server's zone; the API
    // must expose an absolute moment, so interpret via that same zone.
    private static Instant toInstant(LocalDateTime value) {
        return value != null ? value.atZone(ZoneId.systemDefault()).toInstant() : null;
    }
}
