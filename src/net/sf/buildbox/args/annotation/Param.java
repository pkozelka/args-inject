package net.sf.buildbox.args.annotation;

import java.io.File;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    public String format() default "";

    /**
     * sequence separating items of list, if the type is array.
     * The default is {@link File#pathSeparator} because this case is often useful for paths
     */
    public String listSeparator() default "";


    /**
     * symbolic name for use in help
     */
    public String value() default "";
}
