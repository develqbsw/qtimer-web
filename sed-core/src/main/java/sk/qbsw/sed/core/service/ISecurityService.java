package sk.qbsw.sed.core.service;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

/**
 * security domain service interface *
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.0.0
 */
public interface ISecurityService {

	/**
	 * logs user to system
	 *
	 * @param login    user login
	 * @param password user password
	 * @return security token
	 * @throws CSecurityException
	 */
	public CLoggedUserRecord login(String login, String password, final Boolean staySignedIn, final String locale) throws CSecurityException;

	public CLoggedUserRecord loginByAutoLoginToken(String token, String clientLocale) throws CSecurityException;

	/**
	 * log out user from system
	 * 
	 * @param login user login
	 */
	public void logout(String login) throws CSecurityException;
}
