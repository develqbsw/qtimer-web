package sk.qbsw.sed.client.service.business;

import sk.qbsw.sed.client.model.detail.CClientDetailRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationClientRecord;
import sk.qbsw.sed.client.model.security.CClientInfo;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IClientService {

	/**
	 * Adds new organization
	 * 
	 * @param orgToAdd
	 * @return client id
	 * @throws CBusinessException
	 */
	Long add(CRegistrationClientRecord orgToAdd) throws CBusinessException;

	/**
	 * Update flags
	 * 
	 * @param clientInfo
	 * @throws CBusinessException
	 */
	void updateFlags(CClientInfo clientInfo) throws CBusinessException;

	/**
	 * Returns some client detail
	 * 
	 * @param clientId target client identifier
	 * @return client model object
	 * @throws CBusinessException in error case
	 */
	CClientDetailRecord getDetail(Long clientId) throws CBusinessException;

	/**
	 * Updates client record by clienDetail data
	 * 
	 * @param clientDetail input data
	 * @throws CBusinessException in error case
	 */
	public void updateDetail(CClientDetailRecord clientDetail) throws CBusinessException;
}
