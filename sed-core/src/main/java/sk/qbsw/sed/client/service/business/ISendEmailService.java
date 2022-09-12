package sk.qbsw.sed.client.service.business;

import java.util.List;

import sk.qbsw.sed.client.model.codelist.CUserSystemEmailContainer;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.model.domain.CUser;

public interface ISendEmailService {
	
	/**
	 * 
	 * @param email
	 * @return
	 */
	public Long sendMissingEmployeesEmail(String email);

	/**
	 * 
	 * @param data
	 * @return
	 * @throws CSecurityException
	 */
	public Long sendMissingEmployeesEmail(CUserSystemEmailContainer data) throws CSecurityException;

	/**
	 * 
	 * @param warned
	 * @param zoneId
	 * @throws CBusinessException
	 */
	void sendWarningToPresentedEmployees(Long warned, Long zoneId, List<Long> userIds) throws CBusinessException;

	/**
	 * send missing employees email
	 * 
	 * @param adminUser
	 * @param email
	 * @return
	 */
	public Long sendMissingEmployeesEmailMethod(CUser adminUser, String email);
}
