package sk.qbsw.sed.client.service.business;

import sk.qbsw.sed.client.model.lock.CLockDateParameters;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

public interface ILockDateService {

	/**
	 * 
	 * Generated new lock info records for employees without ones. Returns number of
	 * new (generated) lock info records.
	 * 
	 * @param clientId client identifier
	 * @return counter number of new records
	 * @throws CBusinessException in business logic error case
	 * @throws CSecurityException in security login error case
	 */
	public Long generateMissigLockInfoRecords(Long clientId) throws CBusinessException, CSecurityException;

	/**
	 * Updates lock parameters
	 * 
	 * @param lockDateParameters
	 * @return boolean value
	 * @throws CBusinessException in business logic error case
	 * @throws CSecurityException in security login error case
	 */
	public Boolean updateLockDateToSelectedUsers(CLockDateParameters lockDateParameters) throws CBusinessException, CSecurityException;
}
