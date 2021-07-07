package io.github.lucunji.uusiaurinko.block;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Itemize {
    int maxStackSize() default 64;

    boolean genItemModel() default true;
    /**
     * You may need to add "modid:block/" before referenced model name.
     */
    String parentModel() default "";
}
