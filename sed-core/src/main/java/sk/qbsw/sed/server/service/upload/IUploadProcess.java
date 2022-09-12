package sk.qbsw.sed.server.service.upload;

import sk.qbsw.sed.client.response.CUploadResponseContent;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

public interface IUploadProcess {

	/**
	 * 
	 * @param fileRows
	 * @return
	 * @throws CSecurityException
	 */
	public CUploadResponseContent upload(String[] fileRows) throws CSecurityException;
}
