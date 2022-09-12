package sk.qbsw.sed.communication.service;

import sk.qbsw.sed.client.model.params.CParameter;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

public interface IClientParameterClientService {

	/**
	 * Returns parameter object selected by client and parameter name
	 * 
	 * @param clientId client identifier
	 * @param name     parameter name
	 * @return parameter object
	 * @throws CBussinessDataException in business error case
	 */
	public CParameter getClientParameter(Long clientId, String name) throws CBussinessDataException;
}
