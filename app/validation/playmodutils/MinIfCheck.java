/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 *
 * @author mghori
 */
public class MinIfCheck extends AbstractAnnotationCheck<MinIf> {

    final static String mes = "validation.minif";
    Object reference = null;
    String referenceFieldName = null;
    String referenceFieldValue = null;
    double min;

    @Override
    public void configure(MinIf minIf) {
        referenceFieldName = minIf.field();
        referenceFieldValue = minIf.fieldValue();
        min = minIf.min();
        setMessage(minIf.message());
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

        if (reference != null && reference.toString().toUpperCase().equals(referenceFieldValue.toUpperCase())) {
            if (value == null) {
                satisfied = false;
            }
            
            if (value instanceof String) {
                try {
                    satisfied = Double.parseDouble(value.toString()) >= min;
                } catch (Exception e) {
                    satisfied = false;
                }
            }
            
            if (value instanceof Number) {
                try {
                    satisfied = ((Number) value).doubleValue() >= min;
                } catch (Exception e) {
                    satisfied = false;
                }
            }
        } else {
            satisfied = true;
        }

        return satisfied;
    }

    @Override
    public Map<String, String> createMessageVariables() {
        Map<String, String> messageVariables = new HashMap<String, String>();
        messageVariables.put("field", referenceFieldName);
        messageVariables.put("fieldValue", referenceFieldValue);
        messageVariables.put("min", Double.toString(min));
        return messageVariables;
    }
}
