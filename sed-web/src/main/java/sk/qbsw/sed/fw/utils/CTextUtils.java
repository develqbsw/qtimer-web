package sk.qbsw.sed.fw.utils;

import java.text.Normalizer;

public class CTextUtils {

	private CTextUtils() {
		// Auto-generated constructor stub
	}

	private static String stripAccents(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return s;
	}

	/*
	 * Metoda odstrani diakritiku z oboch parametrov a zisti ci sa v stringu nachadza hladany retazec*
	 * @param input - v tomto stringu sa vyhladava
	 * @param searched - tento string sa hlada
	 * @return true ak najde, inak false
	 */
	public static boolean contains(String input, String searched) {
		return stripAccents(input.toLowerCase()).contains(stripAccents(searched.toLowerCase()));
	}
}
