package com.mnp.mobilenumberportability.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;


@ConfigurationProperties(prefix = "mnp.porting")
@Validated
public record PortingProperties(

        /** How long a request may sit PENDING before the background job cancels it. */
        @NotNull Duration requestTimeout,

        /** How often the background job checks for requests that have timed out. */
        @NotNull Duration timeoutCheckInterval
) {
}
