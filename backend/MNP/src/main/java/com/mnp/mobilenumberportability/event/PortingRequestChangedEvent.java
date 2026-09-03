package com.mnp.mobilenumberportability.event;

import com.mnp.mobilenumberportability.dto.PortingRequestResponse;

/**
 * The "subject notifies observers" payload of the Observer pattern used here:
 * {@link com.mnp.mobilenumberportability.service.PortingRequestService} and
 * {@link com.mnp.mobilenumberportability.scheduler.PortingRequestTimeoutScheduler}
 * publish this (via Spring's {@code ApplicationEventPublisher} — itself a
 * built-in implementation of the Observer pattern) whenever a request is
 * created or changes state. {@code PortingRequestNotifier} is the one
 * observer registered on it today, pushing the change out over WebSocket, but
 * any number of independent observers could listen without the publisher
 * knowing they exist.
 */
public record PortingRequestChangedEvent(PortingRequestResponse request) {
}
