package com.mnp.mobilenumberportability.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller parameter to be resolved from the mocked `organization` request
 * header. Stands in for real authentication/authorization per the practicum spec.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentOperator {
}
