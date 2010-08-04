package net.sf.buildbox.args.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * On {@link net.sf.buildbox.args.api.ExecutableCommand command} classes, marks methods that define command options.
 * Number and type of option arguments is constant, and corresponds to method parameters.
 *
 * @see net.sf.buildbox.args.api.ExecutableCommand
 * @see net.sf.buildbox.args.ArgsUtils#stringToType(String, Class)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CliOption {
    /**
     * Name of the option - typically the longer one, like: "--revision"
     *
     * @return -
     */
    public String longName() default "";

    /**
     * Alternate option name, typically the shortened one
     *
     * @return -
     */
    public String shortName() default "";
}
