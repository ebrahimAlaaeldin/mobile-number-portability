package com.mnp.mobilenumberportability.service;

import com.mnp.mobilenumberportability.dto.PageResponse;
import com.mnp.mobilenumberportability.dto.PortingRequestResponse;
import com.mnp.mobilenumberportability.entity.MobileNumber;
import com.mnp.mobilenumberportability.entity.Operator;
import com.mnp.mobilenumberportability.entity.PortingRequest;
import com.mnp.mobilenumberportability.entity.PortingRequestStatus;
import com.mnp.mobilenumberportability.event.PortingRequestChangedEvent;
import com.mnp.mobilenumberportability.exception.DuplicatePendingRequestException;
import com.mnp.mobilenumberportability.exception.NotDonorException;
import com.mnp.mobilenumberportability.exception.PortingRequestNotFoundException;
import com.mnp.mobilenumberportability.exception.SameOperatorPortingException;
import com.mnp.mobilenumberportability.mapper.PortingRequestMapper;
import com.mnp.mobilenumberportability.repository.PortingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PortingRequestService {

    /** Hard cap per the spec ("max of 10 per page") — not just a default, a ceiling. */
    public static final int MAX_PAGE_SIZE = 10;

    private final PortingRequestRepository portingRequestRepository;
    private final PortingRequestMapper portingRequestMapper;
    private final MobileNumberService mobileNumberService;
    private final ApplicationEventPublisher eventPublisher;

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
        PortingRequestResponse response = portingRequestMapper.toResponse(portingRequestRepository.save(request));
        publishChanged(response);
        return response;
    }

    @Transactional(readOnly = true)
    public PageResponse<PortingRequestResponse> findVisibleTo(Operator operator, int page) {
        PageRequest pageRequest = PageRequest.of(page, MAX_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        return PageResponse.from(
                portingRequestRepository.findVisibleTo(operator, pageRequest)
                        .map(portingRequestMapper::toResponse)
        );
    }

    @Transactional
    public PortingRequestResponse accept(Long id, Operator donor) {
        PortingRequest request = getOwnedByDonor(id, donor);
        request.accept();
        // Ownership only actually moves once the donor accepts, not when the request opens.
        request.getMobileNumber().portTo(request.getRecipientOperator(), LocalDate.now());
        PortingRequestResponse response = portingRequestMapper.toResponse(request);
        publishChanged(response);
        return response;
    }

    @Transactional
    public PortingRequestResponse reject(Long id, Operator donor) {
        PortingRequest request = getOwnedByDonor(id, donor);
        request.reject();
        PortingRequestResponse response = portingRequestMapper.toResponse(request);
        publishChanged(response);
        return response;
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

    // Subject side of the Observer pattern: this class has no idea PortingRequestNotifier
    // (or anything else) is listening — it just announces the fact, over Spring's
    // ApplicationEventPublisher, that a request changed.
    private void publishChanged(PortingRequestResponse response) {
        eventPublisher.publishEvent(new PortingRequestChangedEvent(response));
    }
}
