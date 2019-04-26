package structopt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use in enum constants defining subcommands.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface StructoptCommand {
    /**
     * By default, use constant lowecase
     */
    String name() default "";

    Class<?> commandParams();
}
