package sk.qbsw.sed.server.service.security;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sk.qbsw.sed.client.model.security.CClientInfo;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.security.CZoneRecord;
import sk.qbsw.sed.client.ui.component.panel.menu.IUserTypeCode;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.ISessionParametersKeys;
import sk.qbsw.sed.server.dao.IUserDao;
import sk.qbsw.sed.server.dao.IViewOrganizationTreeDao;
import sk.qbsw.sed.server.dao.IZoneDao;
import sk.qbsw.sed.server.exception.CServletSessionUtils;
import sk.qbsw.sed.server.ldap.CLdapAuthentication;
import sk.qbsw.sed.server.ldap.ILdapAuthenticationConfigurator;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.COrganizationTree;
import sk.qbsw.sed.server.model.domain.CUser;
import sk.qbsw.sed.server.model.domain.CZone;
import sk.qbsw.sed.server.model.tree.org.CViewOrganizationTreeNode;
import sk.qbsw.sed.server.service.CEncryptUtils;
import sk.qbsw.sed.server.util.CLocaleUtils;

/**
 * Security management service
 * 
 * @author Dalibor Rak
 * @since 0.1
 * 
 */
@Service
public class CAuthenticateServiceImpl implements IAuthenticateService {
	
	@Autowired
	private IUserDao userDao;

	@Autowired
	private IZoneDao zoneDao;

	@Autowired
	private IViewOrganizationTreeDao viewOrgTreeDao;

	@Autowired
	private ILdapAuthenticationConfigurator ldapAuthenticationConfigurator;

	@Transactional
	@Override
	public CLoggedUserRecord authenticate(final String login, final String password, final Boolean staySignedIn, String locale) throws CSecurityException {
		Logger.getLogger(this.getClass()).debug("Authenticating: " + login);
		// SED-404 Prihlasovanie doménovým používateľom

		if (login.contains("@") || login.contains("admin")) {
			return authenticateViaDb(login, password, staySignedIn, locale);
		} else {
			// 1.) ak prihlasovacie meno neobsahuje znak @ a nie je to admin ani
			// admin_r, overovať v databáze len existenciu používateľa a heslo
			// overovať voči ldap
			return authenticateViaLdap(login, password, staySignedIn, locale);
		}
	}

	private CLoggedUserRecord authenticateViaDb(final String login, final String password, final Boolean staySignedIn, String locale) throws CSecurityException {
		
		CUser user = this.userDao.findByLogin(login);
		if (user == null) {
			throw new CSecurityException(CClientExceptionsMessages.LOGIN_FAILED);
		}

		if (locale != null) {
			// SED-370 pri prihlaseni pouzivatela skontrolovat, ci jazyk pre
			// pouzivatela v databaze je rovnaky ako aktualny, ak nie, tak v DB
			// prepisat
			if (!locale.equals(user.getLanguage())) {
				if (CLocaleUtils.isSupportedLocale(locale)) {
					user.setLanguage(locale);
					this.userDao.saveOrUpdate(user);
				}
			}
		}

		String encryptedUserPassword = user.getPassword();
		String encryptedEnteredPassword = CEncryptUtils.getHash(password, user.getPasswordSalt());
		boolean userPasswordMatchs = encryptedUserPassword.equals(encryptedEnteredPassword);
		if (userPasswordMatchs) {
			final CLoggedUserRecord userRecord = this.convertUserToLoggedUser(user);
			if (staySignedIn != null && staySignedIn) {
				final String autoLoginToken = CEncryptUtils.getHash(user.getLoginLong());
				user.setAutoLoginToken(autoLoginToken.trim());
				final Calendar changeTime = Calendar.getInstance();
				user.setChangeTime(changeTime);
				this.userDao.saveOrUpdate(user);
				userRecord.setAutoLoginToken(autoLoginToken);
			}
			userRecord.getRoles().add(IUserTypeCode.ID_CHANGE_PASSWORD);

			return userRecord;
		}

		Logger.getLogger(this.getClass()).debug("Not authenticated: " + login);

		throw new CSecurityException(CClientExceptionsMessages.LOGIN_FAILED);
	}

	private CLoggedUserRecord authenticateViaLdap(final String login, final String password, final Boolean staySignedIn, String locale) throws CSecurityException {
		boolean successful;

		try {
			// treba importnut certifikat pre LDAP
			// keytool -importcert -file hypnos_CA.crt -keystore
			// "c:\Program Files\Java\jre7\lib\security\cacerts" -alias "hypnos_CA"
			successful = CLdapAuthentication.authenticate(login, password, ldapAuthenticationConfigurator);
		} catch (CBusinessException be) {
			Logger.getLogger(this.getClass()).debug(be);
			throw new CSecurityException(CClientExceptionsMessages.LOGIN_FAILED);
		}

		if (!successful) {
			throw new CSecurityException(CClientExceptionsMessages.LOGIN_FAILED);
		}

		CUser user = this.userDao.findByLogin(login);
		if (user == null) {
			Logger.getLogger(this.getClass()).debug("Not authenticated: " + login);
			throw new CSecurityException(CClientExceptionsMessages.LOGIN_FAILED);
		}

		if (locale != null) {
			// SED-370 pri prihlaseni pouzivatela skontrolovat, ci jazyk pre
			// pouzivatela v databaze je rovnaky ako aktualny, ak nie, tak v DB
			// prepisat
			if (!locale.equals(user.getLanguage())) {
				if (CLocaleUtils.isSupportedLocale(locale)) {
					user.setLanguage(locale);
					this.userDao.saveOrUpdate(user);
				}
			}
		}

		final CLoggedUserRecord userRecord = this.convertUserToLoggedUser(user);
		if (staySignedIn != null && staySignedIn) {
			final String autoLoginToken = CEncryptUtils.getHash(user.getLoginLong());
			user.setAutoLoginToken(autoLoginToken.trim());
			final Calendar changeTime = Calendar.getInstance();
			user.setChangeTime(changeTime);
			this.userDao.saveOrUpdate(user);
			userRecord.setAutoLoginToken(autoLoginToken);
		}

		return userRecord;
	}

	@Transactional(readOnly = true)
	@Override
	public CLoggedUserRecord authenticateByAutoLoginToken(final String token, final String clientLocale) throws CSecurityException {

		CServletSessionUtils.getHttpSession().setAttribute("locale", clientLocale);

		CLoggedUserRecord user = this.getLoggedUserInfo(Boolean.FALSE);
		if ((null == user) && (null != token)) {
			user = this.authenticateByAutoLoginToken(token);
		}
		if (user == null) {
			throw new CSecurityException(CClientExceptionsMessages.LOGIN_FAILED);
		}
		return user;
	}

	private CLoggedUserRecord authenticateByAutoLoginToken(final String token) throws CSecurityException {
		if (!this.canLogin()) {
			throw new CSecurityException(CClientExceptionsMessages.LOGIN_ALREADY_IN);
		}
		Logger.getLogger(this.getClass()).debug("Authenticating with autologin: " + token);

		final CUser user = this.userDao.findByAutoLoginToken(token);
		if (user != null) {
			return this.convertUserToLoggedUser(user);
		}

		// ak sa nenajde user podla tokenu, nic sa nedeje... nejdem vyhadzovat
		// exception... nech sa pekne prihlasi cez meno a heslo...
		return null;
	}

	/**
	 * Clears locks and invalidates session
	 */
	@Override
	public void logout() throws CSecurityException {
		CServletSessionUtils.getHttpSession().setAttribute(ISessionParametersKeys.SECURITY_LOGGED_USER, null);

		CServletSessionUtils.getHttpSession().invalidate();
	}

	private CLoggedUserRecord getLoggedUserInfo(final Boolean throwException) throws CSecurityException {
		return CServletSessionUtils.getLoggedUser(throwException);
	}

	private CLoggedUserRecord convertUserToLoggedUser(final CUser user, boolean checkUserValidFlag) throws CSecurityException {
		if (checkUserValidFlag && !user.getValid()) {
			throw new CSecurityException(CClientExceptionsMessages.USER_BLOCKED);
		}

		// stores info about logged user

		final CClient client = user.getClient();
		final CClientInfo clientInfo = new CClientInfo();
		clientInfo.setClientId(client.getId());
		clientInfo.setClientName(client.getNameShort());
		clientInfo.setProjectRequired(client.getProjectRequired());
		clientInfo.setActivityRequired(client.getActivityRequired());
		clientInfo.setLanguage(client.getLanguage());
		clientInfo.setGenerateMessages(client.getGenerateMessages());

		final CLoggedUserRecord loggedRecord = new CLoggedUserRecord();
		loggedRecord.setClientInfo(clientInfo);
		loggedRecord.setName(user.getName());

		final Long role = this.identifyUsersRole(user, loggedRecord);
		loggedRecord.addRole(role);
		loggedRecord.setRoleName(user.getType().getDescription());
		loggedRecord.setSurname(user.getSurname());
		loggedRecord.setUserId(user.getId());
		loggedRecord.setLogin(user.getLoginLong());
		loggedRecord.setAutoLoginToken(user.getAutoLoginToken());
		loggedRecord.setAllowedAlertnessWork(user.getAllowedAlertnessWork());
		loggedRecord.setHomeOfficePermission(user.getHomeOfficePermission().getId());
		loggedRecord.setTableRows(user.getTableRows());
		loggedRecord.setUserPhotoId(user.getPhoto().getId());
		loggedRecord.setVacation(user.getVacation());
		loggedRecord.setVacationNextYear(user.getVacationNextYear());

		loggedRecord.setJiraAccessToken(user.getJiraAccessToken());
		loggedRecord.setLanguage(user.getLanguage());

		loggedRecord.setRoleCode(this.identifyRoleCode(role));
		loggedRecord.setJiraTokenGeneration(user.getJiraTokenGeneration());

		final List<COrganizationTree> positions = user.getOrganizationTreesOwned();

		if (positions != null && !positions.isEmpty()) {
			loggedRecord.setPosition(positions.get(0).getPossition());
		}

		List<CZoneRecord> zones = new ArrayList<>();

		for (CZone zone : zoneDao.getZones(loggedRecord.getClientInfo().getClientId())) {
			CZoneRecord record = new CZoneRecord();

			record.setId(zone.getId());
			record.setName(zone.getName());

			zones.add(record);
		}

		loggedRecord.getClientInfo().setZones(zones);

		for (CUser favouriteUser : user.getUserFavourites()) {
			loggedRecord.getUserFavourites().add(favouriteUser.getId());
		}

		// when logged user is reception, don't change the logged user record in
		// session!
		// reception hasn't the rights to modify the time records
		CLoggedUserRecord currentLoggedUser = (CLoggedUserRecord) CServletSessionUtils.getHttpSession().getAttribute(ISessionParametersKeys.SECURITY_LOGGED_USER);
		if (currentLoggedUser != null) {
			if (!"RE".equals(currentLoggedUser.getRoleCode())) {
				// logged user isn't reception
				CServletSessionUtils.getHttpSession().setAttribute(ISessionParametersKeys.SECURITY_LOGGED_USER, loggedRecord);
			}
		} else {
			// this is the first user for this session!
			CServletSessionUtils.getHttpSession().setAttribute(ISessionParametersKeys.SECURITY_LOGGED_USER, loggedRecord);
		}

		Logger.getLogger(this.getClass()).debug("Authenticated: " + user.getLoginLong());
		return loggedRecord;
	}

	private CLoggedUserRecord convertUserToLoggedUser(final CUser user) throws CSecurityException {
		return convertUserToLoggedUser(user, true);
	}

	private Long identifyUsersRole(final CUser user, CLoggedUserRecord loggedRecord) {
		if (IUserTypeCode.ID_EMPLOYEE.equals(user.getType().getId())) {
			List<CViewOrganizationTreeNode> list = this.viewOrgTreeDao.findDirectSubordinates(user.getId());

			List<Long> directSubordinateIds = new ArrayList<>();
			for (CViewOrganizationTreeNode node : list) {
				directSubordinateIds.add(node.getUserId());
			}

			loggedRecord.setDirectSubordinates(directSubordinateIds);

			if (list.isEmpty()) {
				return IUserTypeCode.ID_EMPLOYEE_WITHOUT_SUB;
			}

			return IUserTypeCode.ID_EMPLOYEE_WITH_SUB;
		} else {
			return user.getType().getId();
		}
	}

	private String identifyRoleCode(final Long roleId) {
		if (IUserTypeCode.ID_EMPLOYEE.equals(roleId)) {
			return IUserTypeCode.EMPLOYEE;
		} else if (IUserTypeCode.ID_EMPLOYEE_WITH_SUB.equals(roleId)) {
			return IUserTypeCode.EMPLOYEE;
		} else if (IUserTypeCode.ID_EMPLOYEE_WITHOUT_SUB.equals(roleId)) {
			return IUserTypeCode.EMPLOYEE;
		} else if (IUserTypeCode.ID_ACCOUNTANT.equals(roleId)) {
			return IUserTypeCode.ACCOUNTANT;
		} else if (IUserTypeCode.ID_RECEPTION.equals(roleId)) {
			return IUserTypeCode.RECEPTION;
		} else if (IUserTypeCode.ID_SYSTEM_ADMIN.equals(roleId)) {
			return IUserTypeCode.SYSTEM_ADMIN;
		} else if (IUserTypeCode.ID_ORG_ADMIN.equals(roleId)) {
			return IUserTypeCode.ORG_ADMIN;
		} else {
			return "";
		}
	}

	/**
	 * Checks if the session already contains data about logged user
	 */
	private boolean canLogin() throws CSecurityException {
		final CLoggedUserRecord loggedUser = CServletSessionUtils.getLoggedUser(false);
		return loggedUser == null;
	}
}
