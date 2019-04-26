package structopt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Named argument, typically optional.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Structopt {

    char shortOpt() default  0;

    String longOpt() default "";

    String defaultValue() default "";

    // not sure about this - maybe we should just register type convertors in parser
    Class parse() default void.class;
}
