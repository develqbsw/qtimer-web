package sk.qbsw.sed.core.service.impl;

import java.util.Date;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.core.service.ISecurityService;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.service.security.IAuthenticateService;

/**
 * security service
 *
 * @author Podmajersky Lukas
 * @since 2.2.0
 * @version 2.3.0
 */
@Service
public class CSecurityService implements ISecurityService {

	/** security service */
	@Autowired
	private IAuthenticateService authenticateService;

	/** generator for security token */
	private ConfigurablePasswordEncryptor securityTokenGenerator;

	/**
	 * create security service
	 */
	public CSecurityService() {
		securityTokenGenerator = new ConfigurablePasswordEncryptor();
		securityTokenGenerator.setAlgorithm("SHA-1");
		securityTokenGenerator.setPlainDigest(true);
	}

	@Override
	public CLoggedUserRecord login(String login, String password, final Boolean staySignedIn, final String locale) throws CSecurityException {
		CLoggedUserRecord user = authenticateService.authenticate(login, password, staySignedIn, locale);
		if (user != null) {
			StringBuilder securityToken = new StringBuilder(Long.toHexString(new Date().getTime()));
			securityToken.append(user.getLogin());

			user.setSecurityToken(securityTokenGenerator.encryptPassword(securityToken.toString()));
			return user;
		}

		return null;
	}

	@Override
	public CLoggedUserRecord loginByAutoLoginToken(String token, String clientLocale) throws CSecurityException {

		CLoggedUserRecord user = authenticateService.authenticateByAutoLoginToken(token, clientLocale);
		if (user != null) {
			StringBuilder securityToken = new StringBuilder(Long.toHexString(new Date().getTime()));
			securityToken.append(user.getLogin());

			user.setSecurityToken(securityTokenGenerator.encryptPassword(securityToken.toString()));
			return user;
		}

		return null;
	}

	@Override
	public void logout(String login) throws CSecurityException {
		authenticateService.logout();
	}
}
