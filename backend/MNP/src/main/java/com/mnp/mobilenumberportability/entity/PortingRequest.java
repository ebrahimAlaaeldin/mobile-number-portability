package com.mnp.mobilenumberportability.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "porting_requests")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mobile_number_id", nullable = false)
    private MobileNumber mobileNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "donor_operator_id", nullable = false)
    private Operator donorOperator;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_operator_id", nullable = false)
    private Operator recipientOperator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PortingRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    /** Opens a new request in PENDING state for a recipient operator to submit. */
    public static PortingRequest open(MobileNumber mobileNumber, Operator donorOperator, Operator recipientOperator) {
        PortingRequest request = new PortingRequest();
        request.mobileNumber = mobileNumber;
        request.donorOperator = donorOperator;
        request.recipientOperator = recipientOperator;
        request.status = PortingRequestStatus.PENDING;
        return request;
    }

    public void accept() {
        ensurePending();

        this.status = PortingRequestStatus.ACCEPTED;
        this.resolvedAt = LocalDateTime.now();
    }

    public void reject() {
        ensurePending();

        this.status = PortingRequestStatus.REJECTED;
        this.resolvedAt = LocalDateTime.now();
    }

    public void cancel() {
        ensurePending();

        this.status = PortingRequestStatus.CANCELED;
        this.resolvedAt = LocalDateTime.now();
    }

    private void ensurePending() {
        if (this.status != PortingRequestStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending porting requests can be changed"
            );
        }
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = PortingRequestStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
