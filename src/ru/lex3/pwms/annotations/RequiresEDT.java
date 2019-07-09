package ru.lex3.pwms.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that require execution in EDT
 *
 * @author Eugene Matyushkin aka Skipy
 * @version $Id: RequiresEDT.java 422 2010-08-17 13:40:35Z skipy_ru $
 * @see ru.lex3.pwms.annotations.RequiresEDTPolicy
 * @since 13.08.2010
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresEDT {

    /**
     * Execution policy
     *
     * @return execution policy
     */
    RequiresEDTPolicy value() default RequiresEDTPolicy.ASYNC;
}
