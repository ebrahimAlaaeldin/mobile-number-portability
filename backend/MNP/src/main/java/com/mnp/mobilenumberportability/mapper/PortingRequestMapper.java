package com.mnp.mobilenumberportability.mapper;


import com.mnp.mobilenumberportability.dto.PortingRequestResponse;
import com.mnp.mobilenumberportability.entity.PortingRequest;
import org.springframework.stereotype.Component;

@Component
public class PortingRequestMapper {

    public PortingRequestResponse toResponse(PortingRequest request) {
        return new PortingRequestResponse(
                request.getId(),
                request.getMobileNumber().getPhoneNumber(),
                request.getDonorOperator().getName(),
                request.getRecipientOperator().getName(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.getResolvedAt()
        );
    }
}
