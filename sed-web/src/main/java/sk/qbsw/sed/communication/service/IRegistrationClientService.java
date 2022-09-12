package sk.qbsw.sed.communication.service;

import sk.qbsw.sed.client.model.registration.CRegistrationClientRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationUserRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IRegistrationClientService {

	/**
	 * Registers organization and user
	 * 
	 * @param org  org to register
	 * @param user user to register
	 * @throws CBusinessException
	 */
	public void register(CRegistrationClientRecord org, CRegistrationUserRecord user) throws CBussinessDataException;
}
