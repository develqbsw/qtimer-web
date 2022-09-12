package sk.qbsw.sed.server.service.security;

/**
 * Generator of randomly structured message
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
public interface ICodeGenerator {
	
	/**
	 * Generates message with specified length
	 * 
	 * @param characters count of characters
	 * @return generated message
	 */
	public String generateMessage(int characters);

	/**
	 * Generates message with specified number of characters
	 * 
	 * @param characters count of characters to be used for generating message
	 * @param capitals   count of capitals letters
	 * @return message
	 */
	public String generateMessage(int characters, int capitals);
}