package com.mnp.mobilenumberportability.config;

import com.mnp.mobilenumberportability.security.CurrentOperatorArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentOperatorArgumentResolver currentOperatorArgumentResolver;
    private final CorsProperties corsProperties;

    // Register the custom argument resolver for @CurrentOperator annotation
    // This allows Spring to inject the current operator into controller methods
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentOperatorArgumentResolver);
    }

    // The Angular app runs on its own origin (localhost:4200 in dev, a Vercel
    // domain in production) while the API is on its own host — the browser blocks
    // that cross-origin call unless we explicitly allow it here. Origins come from
    // CorsProperties (mnp.cors.allowed-origins) rather than being hardcoded, since
    // frontend and backend are now deployed to separate platforms.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(corsProperties.allowedOrigins().toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*");
    }
}
