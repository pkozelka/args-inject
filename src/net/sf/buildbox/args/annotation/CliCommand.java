package net.sf.buildbox.args.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annottates class representing {@link net.sf.buildbox.args.api.ExecutableCommand command} (a.k.a. subcommand - like "checkout" in "svn checkout").
 *
 * @see net.sf.buildbox.args.api.ExecutableCommand
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CliCommand {
    /**
     * Name of the (sub)command.
     */
    String name();

    /**
     * Alternate names for the (sub)command. Useful for keeping support for deprecated naming, or for shorter variants.
     */
    String[] aliases() default {};
}
