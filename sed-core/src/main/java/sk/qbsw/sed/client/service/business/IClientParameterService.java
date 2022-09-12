package sk.qbsw.sed.client.service.business;

import java.util.List;

import sk.qbsw.sed.client.model.params.CParameter;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

public interface IClientParameterService {

	/**
	 * Returns all client parameters
	 * 
	 * @param clientId client identifier
	 * @return list of parameters
	 * @throws CBusinessException
	 */
	public List<CParameter> getClientParameters(Long clientId) throws CBusinessException;

	/**
	 * Adds a new client parameter object
	 * 
	 * @param clientId    client identifier
	 * @param name        parameter name
	 * @param stringValue parameter string value
	 * @throws CBusinessException in business error case
	 * @throws CSecurityException in security error case
	 */
	public void addClientParameter(Long clientId, String name, String stringValue) throws CBusinessException, CSecurityException;

	/**
	 * Modifies a client parameter object
	 * 
	 * @param parameterId parameter identifier
	 * @param name        parameter name
	 * @param stringValue parameter string value
	 * @throws CBusinessException in business error case
	 * @throws CSecurityException in security error case
	 */
	public void modifyClientParameter(Long parameterId, String name, String stringValue) throws CBusinessException, CSecurityException;

	/**
	 * Returns parameter object selected by client and parameter name
	 * 
	 * @param clientId client identifier
	 * @param name     parameter name
	 * @return parameter object
	 * @throws CBusinessException in business error case
	 */
	public CParameter getClientParameter(Long clientId, String name) throws CBusinessException;
}
