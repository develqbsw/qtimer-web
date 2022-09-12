package sk.qbsw.sed.client.service.business;

import sk.qbsw.sed.client.model.registration.CRegistrationClientRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationUserRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IRegistrationService {

	/**
	 * Registers organization and user
	 * 
	 * @param org  org to register
	 * @param user user to register
	 * @throws CBusinessException
	 */
	public void register(CRegistrationClientRecord org, CRegistrationUserRecord user) throws CBusinessException;
}
