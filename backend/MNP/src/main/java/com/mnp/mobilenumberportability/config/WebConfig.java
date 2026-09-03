package com.mnp.mobilenumberportability.config;

import com.mnp.mobilenumberportability.security.CurrentOperatorArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentOperatorArgumentResolver currentOperatorArgumentResolver;

    // Register the custom argument resolver for @CurrentOperator annotation
    // This allows Spring to inject the current operator into controller methods
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentOperatorArgumentResolver);
    }
}
