package net.kozelka.args.annotation;

import java.lang.annotation.*;

/**
 * On command classes, marks methods that define command options.
 * Number and type of option arguments is constant, and corresponds to method parameters.
 *
 * @see net.kozelka.args.ArgsUtils#stringToType(String, Class)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Option {
    /**
     * Name of the option - typically the longer one, like: "--revision".
     * Supporting long option names is recommended for better readability; users are supposed to use them in scripts.
     *
     * @return -
     */
    public String longName() default "";

    /**
     * Alternate option name, typically the shortened one.
     * Supporting short option names is important for user convenience when this option is supposed to be used frequently.
     *
     * @return -
     */
    public String shortName() default "";

    /**
     * Description to be used in command help
     * @return -
     */
    String description() default "";
}
