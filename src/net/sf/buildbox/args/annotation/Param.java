package net.sf.buildbox.args.annotation;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    public String format() default "";

    /**
     * sequence separating items of list, if the type is array.
     * The default is {@link File#pathSeparator} because this case is often useful for paths
     */
    public String listSeparator() default "";
}
