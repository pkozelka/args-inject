package net.sf.buildbox.args.annotation;

import java.lang.annotation.*;

/**
 * Used to explicitly mark an option as global (or alternatively non-global, if used with value=false).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Global {
    boolean value() default true;
}
