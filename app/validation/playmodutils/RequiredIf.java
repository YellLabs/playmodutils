package validation.playmodutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * This field is required if the value of another field is some value.
 * Message key: validation.requiredif
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(checkWith = RequiredIfCheck.class)
public @interface RequiredIf {
    String message() default RequiredIfCheck.mes;

    /** The value the <code>field</code> must be if this field is mandatory */
    String value();
    
    /** Name of the field to be used for the check */
    String field();
}
