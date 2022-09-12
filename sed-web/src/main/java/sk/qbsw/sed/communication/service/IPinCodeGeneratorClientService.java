package sk.qbsw.sed.communication.service;

import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IPinCodeGeneratorClientService {

	/**
	 * generates PIN
	 * 
	 * @param login
	 * @param oldPINvalue
	 * @return
	 * @throws CBusinessException
	 */
	public String getGeneratedPin(String login, String oldPINvalue) throws CBussinessDataException;
}
