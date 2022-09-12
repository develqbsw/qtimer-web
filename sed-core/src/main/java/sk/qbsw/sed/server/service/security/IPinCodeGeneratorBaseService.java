package sk.qbsw.sed.server.service.security;

import java.util.List;

import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.model.domain.CUser;

public interface IPinCodeGeneratorBaseService {

	/**
	 * Generates unique PIN code with specified length for specific client and user
	 * 
	 * @param minNumChars minimal required numbers of the pin
	 * @param user        user that required the PIN
	 * @param pins        list of used pins
	 * @return generated PIN
	 * @throws CBusinessException in error case
	 */
	public String generatePinCode(int minNumChars, CUser user, List<String> pins, List<String> salts) throws CBusinessException;

	/**
	 * Generates unique PIN code with specified length for specific client and user
	 * 
	 * @param basePinVariation variation for pin base value
	 * @param minNumChars      minimal required numbers of the pin
	 * @param clientId         client identifier
	 * @param email            user email
	 * @param pins             list of used pins
	 * @return generated pin
	 * @throws CBusinessException in error case
	 */
	public String generateUniquePinCode(long basePinVariation, int minNumChars, List<String> pins, List<String> pinSalts, String oldPinValue, String pinSalt) throws CBusinessException;
}
