package validation.playmodutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * This date must be in the future.
 * Message key: validation.future
 * $1: field name
 * $2: reference date
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(checkWith = DateInFutureCheck.class)
public @interface DateInFuture {
    String message() default DateInFutureCheck.mes;

    /** The actual date value to be used a reference */
    String value() default "";
    
    /** Name of the field to be used as the reference */
    String field() default "";
}
