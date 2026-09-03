package com.mnp.mobilenumberportability.service;

import com.mnp.mobilenumberportability.dto.PortingRequestResponse;
import com.mnp.mobilenumberportability.entity.MobileNumber;
import com.mnp.mobilenumberportability.entity.Operator;
import com.mnp.mobilenumberportability.entity.PortingRequest;
import com.mnp.mobilenumberportability.entity.PortingRequestStatus;
import com.mnp.mobilenumberportability.exception.DuplicatePendingRequestException;
import com.mnp.mobilenumberportability.exception.NotDonorException;
import com.mnp.mobilenumberportability.exception.PortingRequestNotFoundException;
import com.mnp.mobilenumberportability.exception.SameOperatorPortingException;
import com.mnp.mobilenumberportability.mapper.PortingRequestMapper;
import com.mnp.mobilenumberportability.repository.PortingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PortingRequestService {

    private final PortingRequestRepository portingRequestRepository;
    private final PortingRequestMapper portingRequestMapper;
    private final MobileNumberService mobileNumberService;

    @Transactional
    public PortingRequestResponse create(String phoneNumber, Operator recipient) {
        MobileNumber mobileNumber = mobileNumberService.resolveOrProvision(phoneNumber);
        Operator donor = mobileNumber.getCurrentOperator();

        if (sameOperator(donor, recipient)) {
            throw new SameOperatorPortingException(phoneNumber);
        }
        if (portingRequestRepository.existsByMobileNumberAndStatus(mobileNumber, PortingRequestStatus.PENDING)) {
            throw new DuplicatePendingRequestException(phoneNumber);
        }

        PortingRequest request = PortingRequest.open(mobileNumber, donor, recipient);
        return portingRequestMapper.toResponse(portingRequestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<PortingRequestResponse> findVisibleTo(Operator operator) {
        return portingRequestRepository.findVisibleTo(operator).stream()
                .map(portingRequestMapper::toResponse)
                .toList();
    }

    @Transactional
    public PortingRequestResponse accept(Long id, Operator donor) {
        PortingRequest request = getOwnedByDonor(id, donor);
        request.accept();
        // Ownership only actually moves once the donor accepts, not when the request opens.
        request.getMobileNumber().portTo(request.getRecipientOperator(), LocalDate.now());
        return portingRequestMapper.toResponse(request);
    }

    @Transactional
    public PortingRequestResponse reject(Long id, Operator donor) {
        PortingRequest request = getOwnedByDonor(id, donor);
        request.reject();
        return portingRequestMapper.toResponse(request);
    }

    private PortingRequest getOwnedByDonor(Long id, Operator donor) {
        PortingRequest request = portingRequestRepository.findById(id)
                .orElseThrow(() -> new PortingRequestNotFoundException(id));

        if (!sameOperator(request.getDonorOperator(), donor)) {
            throw new NotDonorException(id);
        }
        return request;
    }

    // Compare by id rather than entity equals(): the two Operator instances being
    // compared usually come from different persistence contexts (one resolved from the
    // `organization` header, one lazy-loaded off another entity), so they're never the
    // same Java object or Hibernate proxy even when they represent the same row.
    private boolean sameOperator(Operator a, Operator b) {
        return Objects.equals(a.getId(), b.getId());
    }
}
