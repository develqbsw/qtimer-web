package sk.qbsw.sed.communication.service;

import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

public interface IRequestFromEmailService {

	/**
	 * @param requestId
	 * @param requestCode - the unique security request code
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public Boolean approveRequestFromEmail(final String requestId, final String requestCode);

	/**
	 * Rejects request
	 * 
	 * @param requestId
	 * @param requestCode - the unique security request code
	 * @throws CBusinessException
	 * @throws CSecurityException
	 */
	public Boolean rejectRequestFromEmail(final String requestId, final String requestCode);
}
