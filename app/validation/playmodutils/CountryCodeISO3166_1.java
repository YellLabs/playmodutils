package validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;
import play.data.validation.MinSizeCheck;

/**
 * http://en.wikipedia.org/wiki/ISO_3166-1
 * @author Paolo Gentili 38023Pa
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(checkWith = CountryCodeISO3166_1Check.class)
public @interface CountryCodeISO3166_1 {

	String message() default CountryCodeISO3166_1Check.mes;
	
}
