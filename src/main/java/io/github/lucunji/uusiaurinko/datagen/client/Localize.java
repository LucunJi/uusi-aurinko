package io.github.lucunji.uusiaurinko.datagen.client;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface Localize {
    /**
     * Example: {@code {"en_us", "zh_cn"}}
     */
    String[] locales() default {};

    /**
     * Localized strings. Leave it empty to be generated from registry name.
     * <p>
     * Example: {@code {"Dirt", "泥土"}}
     */
    String[] translations() default {};

    /**
     * Auto-generated based on registry name in default.
     */
    String key() default "";

    boolean autoMakeEnUs() default true;
}
