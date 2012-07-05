package validation.playmodutils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

public class CountryCodeISO3166_1Check extends AbstractAnnotationCheck<CountryCodeISO3166_1> {

	final static String mes = "validation.countrycodeISO3166_1";
	
	private final String regex = "(A[DEFGILMNORSTQUWXZ]|" +
								"B[ABDEFGHIJLMNORSTVWYZ]|"+
								"C[ACDFGHIKLMNORUVXYZ]|" +
								"D[EJKMOZ]|" +
								"E[CEGHRST]|" +
								"F[IJKMOR]|" +
								"G[ABDEFGHILMNPQRSTUWY]|" +
								"H[KMNRTU]|" +
								"I[DEQLMNORST]|" +
								"J[EMOP]|" +
								"K[EGHIMNPRWYZ]|" +
								"L[ABCIKRSTUVY]|" +
								"M[ACDEFGHKLMNOQPRSTUVWXYZ]|" +
								"N[ACEFGILOPRUZ]|" +
								"OM|" +
								"P[AEFGHKLMNRSTWY]|" +
								"QA|" +
								"R[EOSUW]|" +
								"S[ABCDEGHIJKLMNORTVYZ]|" +
								"T[CDFGHJKLMNORTVWZ]|" +
								"U[AGMSYZ]|" +
								"V[ACEGINU]|" +
								"W[FS]|" +
								"Y[ET]|" +
								"Z[AMW])";
	
	private Pattern pattern = null;
	
	@Override
	public void configure(CountryCodeISO3166_1 countryCode) {
		setMessage(countryCode.message());
		pattern = Pattern.compile(regex);
	}
	
	@Override
	public boolean isSatisfied(Object validatedObject, Object value, OValContext context, Validator validator) throws OValException {
		requireMessageVariablesRecreation();
		
		if (value==null) {
        	return true;
        }
		
		boolean matches = pattern.matcher(value.toString()).matches();
		return matches;
	}

}
