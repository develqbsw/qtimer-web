package sk.qbsw.sed.server.service.security;

import java.util.List;

import org.springframework.stereotype.Component;

import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.service.CEncryptUtils;

@Component(value = "pinCodeGeneratorBaseService")
public class CPinCodeGeneratorBaseService implements IPinCodeGeneratorBaseService {
	
	static final int MODULE = 17;
	static final int BASE_PIN = 1053;

	/**
	 * @see IPinCodeGeneratorBaseService#generatePinCode(int, CUser, List, List)
	 */
	@Override
	public String generatePinCode(int minNumChars, CUser user, List<String> pinHashs, List<String> salts) throws CBusinessException {
		StringBuilder retVal = new StringBuilder();

		long mstime = System.currentTimeMillis();
		long mstime2 = mstime / 1000;
		mstime = mstime - (mstime2 * 1000);
		retVal.append(generateUniquePinCode(mstime, minNumChars, pinHashs, salts, null, user.getPinCodeSalt()));

		return retVal.toString();
	}

	/**
	 * @see IPinCodeGeneratorBaseService#generateUniquePinCode(long, int, Long,
	 *      String, List, String, String)
	 */
	@Override
	public String generateUniquePinCode(long basePinVariation, int minNumChars, List<String> pinHashs, List<String> pinSalts, String oldPinValue, String userPinSalt) throws CBusinessException {
		// prepare base
		int base = 1;
		if (minNumChars < 1 || minNumChars > 8) {
			minNumChars = 4; // correction for not valid codes
		}
		for (int i = 1; i < minNumChars; i++) // i=1, this is not mistake!
		{
			base *= 10;
		}
		int multiplier = (base + 2 * MODULE) / MODULE;
		base = MODULE * multiplier;

		int testPin = base + Integer.valueOf(Long.toString(basePinVariation));
		// check pins
		if (pinHashs != null || oldPinValue != null) {
			boolean existPinHash;

			pinSalts.add(userPinSalt);
			do {
				existPinHash = false;
				for (String salt : pinSalts) {
					String testPinHash = CEncryptUtils.getHash(Integer.toString(testPin), salt);
					if (pinHashs != null && pinHashs.contains(testPinHash)) {
						existPinHash = true;
						break;
					}
				}
				if (existPinHash || (Integer.toString(testPin).equals(oldPinValue))) {
					testPin += MODULE;
				}
			} while (existPinHash || (Integer.toString(testPin).equals(oldPinValue)));
		}

		return Integer.toString(testPin);
	}
}
