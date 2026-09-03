package com.mnp.mobilenumberportability.dto;


import com.mnp.mobilenumberportability.entity.PortingRequestStatus;

import java.time.Instant;

public record PortingRequestResponse(
        Long id,
        String phoneNumber,
        String donorOperator,
        String recipientOperator,
        PortingRequestStatus status,
        // Instants serialize as UTC ISO-8601 with a trailing 'Z' (e.g.
        // "2026-09-03T18:54:00Z"), so clients in any timezone parse the exact
        // same moment. Never LocalDateTime here: its zone-less format
        // ("2026-09-03T18:54:00") is interpreted as the *client's* local time
        // by `new Date(...)`, shifting the countdown by the server↔client
        // offset (hours) whenever the two zones differ.
        Instant createdAt,
        Instant updatedAt,
        Instant resolvedAt,
        // Absolute deadline for the PENDING auto-cancel, computed server-side
        // from createdAt + mnp.porting.request-timeout. The frontend counts
        // down to this instead of mirroring the timeout value by hand.
        Instant expiresAt
) {
}
