package me.marvin.smp.utils.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation which represents a section/value in a configuration file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Value {
    /**
     * Returns the path of the section/value inside the config.
     *
     * @return the path
     */
    String value();
}
