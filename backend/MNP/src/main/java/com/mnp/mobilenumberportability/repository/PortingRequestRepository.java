package com.mnp.mobilenumberportability.repository;

import com.mnp.mobilenumberportability.entity.MobileNumber;
import com.mnp.mobilenumberportability.entity.Operator;
import com.mnp.mobilenumberportability.entity.PortingRequest;
import com.mnp.mobilenumberportability.entity.PortingRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PortingRequestRepository extends JpaRepository<PortingRequest, Long> {

    /** Used to reject a new request when the number already has one in flight. */
    boolean existsByMobileNumberAndStatus(MobileNumber mobileNumber, PortingRequestStatus status);

    /** Picked up by the background job that times out stale pending requests. */
    List<PortingRequest> findAllByStatusAndCreatedAtBefore(PortingRequestStatus status, LocalDateTime cutoff);

    /**
     * A request is visible to an operator if it's the donor or recipient on it (any status),
     * or if it has already been accepted (visible to everyone once settled).
     */
    @Query("""
            select pr from PortingRequest pr
            where pr.donorOperator = :operator
               or pr.recipientOperator = :operator
               or pr.status = com.mnp.mobilenumberportability.entity.PortingRequestStatus.ACCEPTED
            order by pr.createdAt desc
            """)
    List<PortingRequest> findVisibleTo(@Param("operator") Operator operator);
}
