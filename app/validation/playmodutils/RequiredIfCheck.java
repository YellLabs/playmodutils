/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validation.playmodutils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 *
 * @author mghori
 */
public class RequiredIfCheck extends AbstractAnnotationCheck<RequiredIf> {

    final static String mes = "validation.requiredif";
    Object reference = null;
    String referenceFieldName = null;
    String referenceFieldValue = null;

    @Override
    public void configure(RequiredIf requiredIf) {
        referenceFieldName = requiredIf.field();
        referenceFieldValue = requiredIf.value();
        setMessage(requiredIf.message());
    }

    @Override
    public boolean isSatisfied(Object validatedObject, Object value, OValContext context, Validator validator) {
        boolean satisfied = false;

        requireMessageVariablesRecreation();
        if (referenceFieldName != null) {
            try {
                // get the type for the reference field from the validatedObject
                Field referenceField = validatedObject.getClass().getDeclaredField(referenceFieldName);
                reference = referenceField.get(validatedObject);
            } catch (NoSuchFieldException ex) {
                // this should not happen
            } catch (SecurityException ex) {
                // this should not happen
            } catch (IllegalAccessException ex) {
                // this should not happen
            }
        }

        if (reference != null) {
            satisfied = reference.toString().toUpperCase().equals(referenceFieldValue.toUpperCase()) && value != null;
        } else {
            satisfied = true;
        }

        return satisfied;
    }

    @Override
    public Map<String, String> createMessageVariables() {
        Map<String, String> messageVariables = new TreeMap<String, String>();
        messageVariables.put("field", referenceFieldName);
        messageVariables.put("value", referenceFieldValue);
        return messageVariables;
    }
}
