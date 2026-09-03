package com.mnp.mobilenumberportability.scheduler;

import com.mnp.mobilenumberportability.config.PortingProperties;
import com.mnp.mobilenumberportability.entity.PortingRequest;
import com.mnp.mobilenumberportability.entity.PortingRequestStatus;
import com.mnp.mobilenumberportability.repository.PortingRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Background sweep that cancels porting requests the donor never acted on in time
 * (the "Request timed out" transition in the porting request state diagram).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PortingRequestTimeoutScheduler {

    private final PortingRequestRepository portingRequestRepository;
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

        log.info("Canceled {} pending porting request(s) past the {} timeout",
                expired.size(), portingProperties.requestTimeout());
    }
}
