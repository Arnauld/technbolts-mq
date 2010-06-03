package org.technbolts.storage.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Association {

    Mode value() default Mode.Inline;

    public enum Mode {
        Reference,
        Inline
    }
}
