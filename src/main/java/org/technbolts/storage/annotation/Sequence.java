package org.technbolts.storage.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Sequence {
    int versionMin() default -1;
    int versionMax() default Integer.MAX_VALUE;
    String name() default Constant.DEFAULT;
}