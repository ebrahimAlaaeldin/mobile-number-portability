package com.mnp.mobilenumberportability.scheduler;

import com.mnp.mobilenumberportability.config.PortingProperties;
import com.mnp.mobilenumberportability.entity.PortingRequest;
import com.mnp.mobilenumberportability.entity.PortingRequestStatus;
import com.mnp.mobilenumberportability.event.PortingRequestChangedEvent;
import com.mnp.mobilenumberportability.mapper.PortingRequestMapper;
import com.mnp.mobilenumberportability.repository.PortingRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class PortingRequestTimeoutScheduler {

    private final PortingRequestRepository portingRequestRepository;
    private final PortingRequestMapper portingRequestMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final PortingProperties portingProperties;

    @Scheduled(fixedDelayString = "${mnp.porting.timeout-check-interval:PT30S}")
    @Transactional
    public void cancelExpiredRequests() {
        LocalDateTime cutoff = LocalDateTime.now().minus(portingProperties.requestTimeout());

        List<PortingRequest> expired = portingRequestRepository
                .findAllByStatusAndCreatedAtBefore(PortingRequestStatus.PENDING, cutoff);

        if (expired.isEmpty()) {
            return;
        }

        // Managed entities: mutating them here is enough, no explicit save needed —
        // the change flushes when this @Transactional method returns.
        expired.forEach(PortingRequest::cancel);

        // Same Observer-pattern announcement the service makes on accept/reject — a
        // timeout is just another way a request changes state, so it gets the same
        // instant push to any connected client watching it.
        expired.forEach(request ->
                eventPublisher.publishEvent(new PortingRequestChangedEvent(portingRequestMapper.toResponse(request))));

        log.info("Canceled {} pending porting request(s) past the {} timeout",
                expired.size(), portingProperties.requestTimeout());
    }
}
