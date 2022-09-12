package sk.qbsw.sed.server.util;

import java.util.Locale;

import sk.qbsw.sed.client.model.ILanguageConstant;

public class CLocaleUtils {

	private CLocaleUtils() {
		// Auto-generated constructor stub
	}

	public static boolean isSupportedLocale(final String locale) {
		return (ILanguageConstant.SK.equals(locale) || ILanguageConstant.EN.equals(locale));
	}

	/**
	 * 
	 * @return Locale object
	 */
	public static Locale getLocale(String language) {
		return new Locale(language);
	}

	public static Locale getLocale() {
		return new Locale("sk");
	}
}
