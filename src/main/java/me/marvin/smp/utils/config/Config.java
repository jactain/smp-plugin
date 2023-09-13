package me.marvin.smp.utils.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation which represents a class as a YAML configuration file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config {
    /**
     * Returns the name of the configuration file without the extension.
     *
     * @return the name of the file
     */
    String value();
}
