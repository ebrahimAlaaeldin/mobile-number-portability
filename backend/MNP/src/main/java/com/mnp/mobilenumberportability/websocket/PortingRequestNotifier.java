package com.mnp.mobilenumberportability.websocket;

import com.mnp.mobilenumberportability.event.PortingRequestChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * The Observer: reacts to {@link PortingRequestChangedEvent} by broadcasting
 * it to every connected WebSocket client. It knows nothing about who
 *
 * {@code @TransactionalEventListener(AFTER_COMMIT)} rather than the plain so the push only fires once the change is
 * actually durable in the database.

 */
@Component
@RequiredArgsConstructor
public class PortingRequestNotifier {

    private static final String DESTINATION = "/topic/porting-requests";

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPortingRequestChanged(PortingRequestChangedEvent event) {
        messagingTemplate.convertAndSend(DESTINATION, event.request());
    }
}
