package sk.qbsw.sed.server.util;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class CRandomStringGenerator {

	private static final String UPPER_ALPHABETIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static final String LOWER_ALPHABETIC_CHARACTERS = UPPER_ALPHABETIC_CHARACTERS.toLowerCase();

	private static final String NUMERIC_CHARACTERS = "01234567890";

	private final Random random = new Random();

	public String generate(final int upperCharsCount, final int lowerCharsCount, final int numericCharsCount) {
		String retVal = "";
		retVal += this.generateFromSequence(UPPER_ALPHABETIC_CHARACTERS, upperCharsCount);
		retVal += this.generateFromSequence(LOWER_ALPHABETIC_CHARACTERS, lowerCharsCount);
		retVal += this.generateFromSequence(NUMERIC_CHARACTERS, numericCharsCount);
		retVal = this.randomReorder(retVal);
		return retVal;
	}

	private String randomReorder(final String value) {
		final char[] chars = value.toCharArray();
		for (int i = 0; i < value.length() - 1; i++) {
			final int index1 = this.getInt(value.length() - 1);
			final int index2 = this.getInt(value.length() - 1);
			final char tmp = chars[index1];
			chars[index1] = chars[index2];
			chars[index2] = tmp;
		}
		return String.valueOf(chars);
	}

	private String generateFromSequence(final String sequence, final int count) {
		String retVal = "";
		for (int i = 0; i < count; i++) {
			retVal += sequence.charAt(this.getInt(sequence.length() - 1));
		}
		return retVal;
	}

	private int getInt(final int min, final int max) {
		if (max > min) {
			return this.random.nextInt((max + 1) - min) + min;
		}
		return min;
	}

	private int getInt(final int max) {
		return this.getInt(0, max);
	}
}
