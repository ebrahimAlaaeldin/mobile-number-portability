package com.mnp.mobilenumberportability.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Origins allowed to call the {@code /api/**} endpoints (see {@link WebConfig}).
 * Kept out of code so the same jar can be pointed at whatever origin actually
 * hosts the frontend (local dev, a Vercel deployment, ...) without a rebuild.
 */
@ConfigurationProperties(prefix = "mnp.cors")
@Validated
public record CorsProperties(

        /**
         * Origin patterns (Spring's {@code allowedOriginPatterns} syntax, so
         * "https://*.vercel.app" matches every preview/prod deployment) allowed
         * to call the API.
         */
        @NotEmpty List<String> allowedOrigins
) {
}
