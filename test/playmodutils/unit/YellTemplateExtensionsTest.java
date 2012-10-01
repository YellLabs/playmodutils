package playmodutils.unit;

import org.junit.Test;

import play.test.UnitTest;

import static com.yell.civitas.play.utils.template.YellTemplateExtensions.escapeJavaScriptYell;

public class YellTemplateExtensionsTest extends UnitTest {
	
	private static final String ANY_TEXT_WITHOUT_SPECIAL_CHARACTERS = "ANY TEXT";
	private static final String A_TEXT_WITH_POUND_SIGN = "TEXT £££ TEXT";
	private static final String A_TEXT_WITH_SINGLE_QUOTES = "TEXT ' TEXT '' TEXT";

	@Test
	public void testThatAnyTextWithoutSpecialCharactersRaminsUnchanged() {
		assertEquals(ANY_TEXT_WITHOUT_SPECIAL_CHARACTERS, escapeJavaScriptYell(ANY_TEXT_WITHOUT_SPECIAL_CHARACTERS));
	}
	
	@Test
	public void testThatSingleQuoteSignIsNotEscaped() {
		assertEquals(A_TEXT_WITH_SINGLE_QUOTES, escapeJavaScriptYell(A_TEXT_WITH_SINGLE_QUOTES));
	}

	@Test
	public void testThatPoundSignIsNotEscaped() {
		assertEquals(A_TEXT_WITH_POUND_SIGN, escapeJavaScriptYell(A_TEXT_WITH_POUND_SIGN));
	}

}
