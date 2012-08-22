package validation.playmodutils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import play.Logger;
import play.Play;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;


/**
 * This checker validates a postcode using a different regular expression depending
 * on the value of the 'country' field. 
 * @author Paolo Gentili 38023Pa
 */
public class LocalizedRegExMatchCheck extends AbstractAnnotationCheck<LocalizedRegExMatch> {

	final static String mes = "validation.countrymatch";
    
	private static final String ukPostcodeRegex = 
		Play.configuration.getProperty("ukPostcodeRegex", "(GIR 0AA|[A-PR-UWYZ]([0-9][0-9A-HJKPS-UW]?|[A-HK-Y][0-9][0-9ABEHMNPRV-Y]?) ?[0-9][ABD-HJLNP-UW-Z]{0,2})");
	
	private static final String usPostcodeRegex = 
		Play.configuration.getProperty("usPostcodeRegex", "^\\d{5}(-\\d{4})?$");
	
	private static final String caPostcodeRegex = 
		Play.configuration.getProperty("caPostcodeRegex", "^[ABCEGHJKLMNPRSTVXY]{1}\\d{1}[A-Z]{1} *\\d{1}[A-Z]{1}\\d{1}$");
		
	private static final String esPostcodeRegex = 
		Play.configuration.getProperty("esPostcodeRegex", "^([1-9]{2}|[0-9][1-9]|[1-9][0-9])[0-9]{3}$");	
	
	private String countryFieldName = null;
    private Object countryFieldValue = null;
    
    private String localizedFieldName = null;
    private String localizedFieldValue = null;
    
    private static Map<String, String> patterns = null;
    
	@Override
	public void configure(LocalizedRegExMatch countryMatch) {

		patterns = new HashMap<String, String>();
		patterns.put("GB", ukPostcodeRegex);
		patterns.put("US", usPostcodeRegex);
		patterns.put("CA", caPostcodeRegex);
		patterns.put("ES", esPostcodeRegex);

        countryFieldName = countryMatch.value();
        setMessage(countryMatch.message());
	}

	@Override
	public boolean isSatisfied(Object validatedObject, Object value, OValContext context, Validator validator) throws OValException {
		requireMessageVariablesRecreation(); 
        try {
            // get the type for the reference field from the validatedObject
            Field countryField = validatedObject.getClass().getDeclaredField(countryFieldName);
            
            if (countryField!=null) 
            	countryFieldValue = countryField.get(validatedObject);
            
            if (context!=null) 
            	localizedFieldName = context.toString();
            
            if (value!=null) 
            	localizedFieldValue = value.toString().toUpperCase();
            
        } catch (NoSuchFieldException ex) {
            // this should not happen
        } catch (SecurityException ex) {
            // this should not happen
        } catch (IllegalAccessException ex) {
            // this should not happen
        }

        if (countryFieldValue==null) {
        	Logger.warn("couldn't validate field %s against field %s " +
        		"as field %s is missing" , localizedFieldName, countryFieldName,
        			countryFieldName);        	
        	return false;
        }
        
		if (patterns.get(countryFieldValue)==null) {
        	Logger.warn("received a valid country code [%s] " +
        			"but we don't have any regexp to match it against. " +
        			"Validation of [%s] with value [%s] will pass the current constraint ..."
        			, countryFieldValue, localizedFieldName, localizedFieldValue);
            return true;
        }
        
        if (localizedFieldValue == null || localizedFieldValue.length() == 0) {
        	Logger.warn("received an empty [%s] with value [%s], " +
        			"validation will pass the current constraint..."
        			, localizedFieldName, localizedFieldValue);
            return true;
        }

        boolean matches = Pattern.compile(patterns.get(countryFieldValue))
        							.matcher(localizedFieldValue).matches();
        
		return matches;
	}

    @Override
    public Map<String, String> createMessageVariables() {
        Map<String, String> messageVariables = new HashMap<String, String>();

        // !!!! changing which variables this map contains
        //      will affect the error message formatting for this constraint. 
        //      In particular the variables will be substituted in a different order !!!!! 
        messageVariables.put("countryFieldName", countryFieldName);
        messageVariables.put("countryFieldValue", (String) countryFieldValue);
        messageVariables.put("localizedFieldValue", localizedFieldValue);
        messageVariables.put("requiredPattern", patterns.get(countryFieldValue));
        
        return messageVariables;
    }

}
