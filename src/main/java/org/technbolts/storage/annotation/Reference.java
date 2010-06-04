package org.technbolts.storage.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author <a href="mailto:arnauld.loyer@gmail.com">Loyer Arnauld</a>
 * @version $Revision$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Reference {
    int versionMin() default -1;
    int versionMax() default Integer.MAX_VALUE;
    String store() default Constant.DEFAULT;
}
