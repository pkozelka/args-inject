package net.sf.buildbox.args.annotation;

import java.lang.annotation.*;

/**
 * Annottates class representing subcommand (like "checkout" in "svn checkout").
 * It is expected to implement {@link java.util.concurrent.Callable Callable&lt;Integer&gt;}.
 * @see java.util.concurrent.Callable
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SubCommand {
    /**
     * Name of the (sub)command.
     */
    String name() default "";

    /**
     * Alternate names for the (sub)command. Useful for keeping support for deprecated naming, or for shorter variants.
     */
    String[] aliases() default {};

    /**
     * Description to be used in command help
     */
    String description() default "";
}
