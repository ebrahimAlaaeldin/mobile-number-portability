package com.mnp.mobilenumberportability.controller;

import com.mnp.mobilenumberportability.dto.CreatePortingRequestRequest;
import com.mnp.mobilenumberportability.dto.PortingRequestResponse;
import com.mnp.mobilenumberportability.entity.Operator;
import com.mnp.mobilenumberportability.security.CurrentOperator;
import com.mnp.mobilenumberportability.service.PortingRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/porting-requests")
@RequiredArgsConstructor
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
    // requests that have already been accepted.
    @GetMapping
    public List<PortingRequestResponse> list(@CurrentOperator Operator operator) {
        return portingRequestService.findVisibleTo(operator);
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
