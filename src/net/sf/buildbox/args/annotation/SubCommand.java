package net.sf.buildbox.args.annotation;

import java.lang.annotation.*;

/**
 * Annottates class representing {@link net.sf.buildbox.args.api.ExecutableCommand command} (a.k.a. subcommand - like "checkout" in "svn checkout").
 *
 * @see net.sf.buildbox.args.api.ExecutableCommand
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
