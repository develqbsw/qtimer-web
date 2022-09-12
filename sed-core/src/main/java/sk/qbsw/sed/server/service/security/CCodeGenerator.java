package sk.qbsw.sed.server.service.security;

import org.springframework.stereotype.Component;

/**
 * Service for generating random message
 * 
 * @author Dalibor Rak
 * @version 0.1
 */
@Component(value = "codeGenerator")
public class CCodeGenerator implements ICodeGenerator {
	/**
	 * Generates message with specified number of characters
	 * 
	 * @param characters count of characters to be used for generating message
	 * @return message
	 */
	public String generateMessage(int characters) {
		return generate(characters, 65, 90);
	}

	/**
	 * Generates message with specified number of characters
	 * 
	 * @param characters count of characters to be used for generating message
	 * @param capitals   count of capitals letters
	 * @return message
	 */
	public String generateMessage(int characters, int capitals) {
		String s1 = generate(1, 65, 90);
		String s2 = generate(characters - 1, 97, 122);

		return s1 + s2;
	}

	/**
	 * Generate text sizes
	 * 
	 * @param characters
	 * @param min        minimum character used by the generation
	 * @param max        maximum character used by the generation
	 * @return generated text
	 */
	private String generate(int characters, int min, int max) {
		String retVal = "";

		for (int i = 0; i < characters; i++) {
			int add = 0;
			do {
				add = (int) Math.round(Math.random() * max);
			} while (add < min || add > max); // search just capitals

			retVal += Character.toString((char) add);
		}

		return retVal;
	}
}
