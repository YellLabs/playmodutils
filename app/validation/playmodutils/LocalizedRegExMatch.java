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
@Constraint(checkWith = LocalizedRegExMatchCheck.class)
public @interface LocalizedRegExMatch {
    String message() default LocalizedRegExMatchCheck.mes;
    
    String value() default "country";
    
}
