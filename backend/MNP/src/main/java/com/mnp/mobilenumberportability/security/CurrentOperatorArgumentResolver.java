package com.mnp.mobilenumberportability.security;

import com.mnp.mobilenumberportability.entity.Operator;
import com.mnp.mobilenumberportability.exception.UnknownOperatorException;
import com.mnp.mobilenumberportability.repository.OperatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@Component
@RequiredArgsConstructor
public class CurrentOperatorArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String ORGANIZATION_HEADER = "organization";

    private final OperatorRepository operatorRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentOperator.class)
                && Operator.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                   NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String organization = webRequest.getHeader(ORGANIZATION_HEADER);
        if (!StringUtils.hasText(organization)) {
            throw new UnknownOperatorException("Missing required '" + ORGANIZATION_HEADER + "' header");
        }

        return operatorRepository.findByOrganization(organization.trim().toLowerCase())
                .orElseThrow(() -> new UnknownOperatorException("Unknown organization: " + organization));
    }
}
