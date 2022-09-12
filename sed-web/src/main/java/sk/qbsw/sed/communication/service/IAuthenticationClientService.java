package sk.qbsw.sed.communication.service;

import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.model.CLoggedUser;

public interface IAuthenticationClientService {
	/**
	 * Authenticates user with login and password
	 * 
	 * @param login
	 * @param password
	 */
	public CLoggedUser authenticate(String login, String password, final Boolean staySignedIn, final String locale) throws CBussinessDataException;

	/**
	 * Authenticates user with session or autologin token
	 * 
	 * @param token
	 * @return
	 * @throws CSecurityException
	 */
	public CLoggedUser authenticateByAutoLoginToken(String token, String clientLocale) throws CBussinessDataException;

	/**
	 * Loggs the user out (invalidates session)
	 */
	public void logout(String login) throws CBussinessDataException;
}
