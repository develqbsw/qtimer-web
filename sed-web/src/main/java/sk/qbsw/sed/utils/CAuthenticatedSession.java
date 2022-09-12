package sk.qbsw.sed.utils;

import java.util.Locale;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.communication.service.IAuthenticationClientService;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.model.CLoggedUser;

/**
 * Session of the logged user
 * 
 * @author Lobb
 */
public class CAuthenticatedSession extends AuthenticatedWebSession {

	@SpringBean
	private IAuthenticationClientService loginService;

	private CLoggedUser user;

	private CSecurityException securityException;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CAuthenticatedSession(Request request) {
		super(request);
		Injector.get().inject(this);
	}

	/**
	 * Gets actual session
	 * 
	 * @return active session
	 */
	public static CAuthenticatedSession get() {
		return (CAuthenticatedSession) AuthenticatedWebSession.get();
	}

	public final void signIn(final String username, final String password, boolean staySignedIn) throws CBussinessDataException {
		authenticate(username, password, staySignedIn);
		signIn(true);
		bind();
	}

	public final boolean signIn(final String autoLoginToken) {
		boolean signedIn = authenticate(autoLoginToken);
		signIn(signedIn);

		if (signedIn) {
			bind();
		}
		return signedIn;
	}

	@Override
	public boolean authenticate(String username, String password) {
		throw new NotImplementedException(""); // tato metoda sa volat nesmie
	}

	private void authenticate(String login, String password, boolean staySignedIn) throws CBussinessDataException {
		user = loginService.authenticate(login, password, staySignedIn, getLocale().getLanguage());

		if (staySignedIn) {
			// ak chcem zostat prihlaseny...
			Cookie autoLoginCookie = new Cookie(ICookiesConstants.COOKIE_NAME_AUTO_LOGIN, user.getRecord().getAutoLoginToken());
			autoLoginCookie.setMaxAge(7 * 24 * 60 * 60);

			WebResponse webResponse = (WebResponse) RequestCycle.get().getResponse();
			webResponse.addCookie(autoLoginCookie);
		}
	}

	private boolean authenticate(String autoLoginToken) {
		try {
			user = loginService.authenticateByAutoLoginToken(autoLoginToken, getLocale().getLanguage());
			setLocale(new Locale(user.getRecord().getLanguage()));

			return true;
		} catch (CBussinessDataException e) {
			Logger.getLogger(this.getClass()).info(e);
		}
		return false;
	}

	public boolean hasRole(String role) {
		Boolean hasRole;
		if (getRoles() == null) {
			hasRole = Boolean.FALSE;
		} else {
			hasRole = getRoles().hasAnyRole(new Roles(role));
		}
		return hasRole;
	}

	@Override
	public Roles getRoles() {
		if (isSignedIn() && user.getRecord() != null) {
			Roles roles = new Roles();
			roles.add(user.getRecord().getRoleCode());
			return roles;
		}
		return null;
	}

	public CSecurityException getSecurityException() {
		return securityException;
	}

	public void setSecurityException(CSecurityException securityException) {
		this.securityException = securityException;
	}

	public CLoggedUserRecord getUser() {
		if (user == null) {
			invalidate();
			return null;
		}
		return user.getRecord();
	}

	public CLoggedUser getUserModel() {
		if (user == null) {
			invalidate();
			return null;
		}
		return user;
	}

	public void setUser(CLoggedUser user) {
		this.user = user;
	}

	public void logout() {
		try {
			loginService.logout(getUser().getLogin());
		} catch (CBussinessDataException e) {
			Logger.getLogger(this.getClass()).info(e);
		}

		// ak sa bez problemov odhlasim, vymazem si cookies...
		WebResponse webResponse = (WebResponse) RequestCycle.get().getResponse();
		webResponse.clearCookie(new Cookie(ICookiesConstants.COOKIE_NAME_AUTO_LOGIN, null));

		invalidate();
	}
}
