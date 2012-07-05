/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validation.playmodutils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import play.exceptions.UnexpectedException;
import play.utils.Utils.AlternativeDateFormat;

/**
 *
 * @author mghori
 */
public class DateInFutureCheck extends AbstractAnnotationCheck<DateInFuture> {

    final static String mes = "validation.future";
    Date reference = null;
    String referenceFieldName = null;

    @Override
    public void configure(DateInFuture future) {
        try {
            if (future.field().isEmpty()) {
                this.reference = future.value().equals("") ? new Date() : AlternativeDateFormat.getDefaultFormatter().parse(future.value());
            } else {
                referenceFieldName = future.field();
            }
        } catch (ParseException ex) {
            throw new UnexpectedException("Cannot parse date " + future.value(), ex);
        }

        if (!future.value().isEmpty() && future.message().equals(mes)) {
            setMessage("validation.after");
        } else {
            setMessage(future.message());
        }
    }

    @Override
    public boolean isSatisfied(Object validatedObject, Object value, OValContext context, Validator validator) {
        boolean satisfied = false;

        requireMessageVariablesRecreation();
        if (reference == null && referenceFieldName != null) {
            try {
                // get the type for the reference field from the validatedObject
                Field referenceField = validatedObject.getClass().getDeclaredField(referenceFieldName);
                if (referenceField != null && referenceField.getType().isAssignableFrom(Date.class)) {
                    reference = (Date) referenceField.get(validatedObject);
                }
            } catch (NoSuchFieldException ex) {
                // this should not happen
            } catch (SecurityException ex) {
                // this should not happen
            } catch (IllegalAccessException ex) {
                // this should not happen
            }
        }
        
        if (value == null) {
            satisfied = true;
        } else if (value instanceof Date) {
            try {
                if (reference != null) {
                    satisfied = reference.before((Date) value);
                } else {
                    satisfied = true;
                }
            } catch (Exception e) {
                satisfied = false;
            }
        }

        return satisfied;
    }

    @Override
    public Map<String, String> createMessageVariables() {
        Map<String, String> messageVariables = new HashMap<String, String>();
        messageVariables.put("reference", new SimpleDateFormat("yyyy-MM-dd").format(reference));
        return messageVariables;
    }
}
