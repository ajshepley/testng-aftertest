package com.github.ajshepley;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SafeAfterTestMethod
 *
 * Test classes annotated with SafeAfterTestMethod will be executed after the test completes.
 * If that method throws an exception, the test method will fail, but the test will continue as
 * configured.
 *
 * At least one executed test class must have SafeAfterTestMethodListener as a listener
 * for this annotation to be processed.
 *
 * @see SafeAfterTestMethodListener
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SafeAfterTestMethod {
  // Lower priority values are processed first.
  int priority() default 0;
}
