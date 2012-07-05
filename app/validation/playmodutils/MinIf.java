package validation.playmodutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * This field must have a minimum value if the value of another field is some value.
 * Message key: validation.minif
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(checkWith = MinIfCheck.class)
public @interface MinIf {
    String message() default MinIfCheck.mes;

    /** The value the <code>field</code> must be if this field is mandatory */
    String fieldValue();
    
    /** Name of the field to be used for the check */
    String field();
    
    /** The minimum value for this field */
    double min();
}
