package sk.qbsw.sed.client.service.business;

import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IPinCodeGeneratorService {
	
	/**
	 * generates PIN
	 * 
	 * @param login
	 * @param oldPINvalue
	 * @return
	 * @throws CBusinessException
	 */
	public String getGeneratedPIN(String login, String oldPINvalue) throws CBusinessException;
}
