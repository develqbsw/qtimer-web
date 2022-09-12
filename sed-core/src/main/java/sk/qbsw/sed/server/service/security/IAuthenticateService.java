package sk.qbsw.sed.server.service.security;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

public interface IAuthenticateService {

	/**
	 * Authenticates user with login and password
	 * 
	 * @param login
	 * @param password
	 */
	public CLoggedUserRecord authenticate(String login, String password, final Boolean staySignedIn, final String locale) throws CSecurityException;

	/**
	 * Authenticates user with session or autologin token
	 * 
	 * @param token
	 * @return
	 * @throws CSecurityException
	 */
	public CLoggedUserRecord authenticateByAutoLoginToken(String token, String clientLocale) throws CSecurityException;

	/**
	 * Loggs the user out (invalidates session)
	 */
	public void logout() throws CSecurityException;
}
