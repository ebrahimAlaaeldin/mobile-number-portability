package com.mnp.mobilenumberportability.controller;

import com.mnp.mobilenumberportability.dto.CreatePortingRequestRequest;
import com.mnp.mobilenumberportability.dto.PageResponse;
import com.mnp.mobilenumberportability.dto.PortingRequestResponse;
import com.mnp.mobilenumberportability.entity.Operator;
import com.mnp.mobilenumberportability.security.CurrentOperator;
import com.mnp.mobilenumberportability.service.PortingRequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/porting-requests")
@RequiredArgsConstructor
@Validated
public class PortingRequestController {

    private final PortingRequestService portingRequestService;

    // The caller is the Recipient: "as a mobile network operator (Recipient), I want to
    // submit a porting request".
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PortingRequestResponse create(@Valid @RequestBody CreatePortingRequestRequest request,
                                          @CurrentOperator Operator recipient) {
        return portingRequestService.create(request.phoneNumber(), recipient);
    }

    // Donor/recipient see every request they're party to; everyone else only sees
    // requests that have already been accepted. Paginated, 10 per page (see
    // PortingRequestService.MAX_PAGE_SIZE) — `page` is 0-indexed.
    @GetMapping
    public PageResponse<PortingRequestResponse> list(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                       @CurrentOperator Operator operator) {
        return portingRequestService.findVisibleTo(operator, page);
    }

    @PostMapping("/{id}/accept")
    public PortingRequestResponse accept(@PathVariable Long id, @CurrentOperator Operator donor) {
        return portingRequestService.accept(id, donor);
    }

    @PostMapping("/{id}/reject")
    public PortingRequestResponse reject(@PathVariable Long id, @CurrentOperator Operator donor) {
        return portingRequestService.reject(id, donor);
    }
}
