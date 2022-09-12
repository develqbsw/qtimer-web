package sk.qbsw.sed.server.exception;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.ISessionParametersKeys;

/**
 * Class reads HttpSession from thread locale. It uses
 * org.springframework.web.context.request.RequestContextListener from spring
 * library (it must be part of web.xml)
 * 
 * @author Dalibor Rak
 * @version 0.1
 * 
 */
public class CServletSessionUtils {

	private CServletSessionUtils() {
		// Auto-generated constructor stub
	}

	/**
	 * Reads Http session from Thread locale using spring library
	 * 
	 * @return HttpSession
	 */
	public static HttpServletRequest getHttpRequest() {
		final ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return (ra).getRequest();

	}

	/**
	 * Reads Http session from Thread locale using spring library
	 * 
	 * @return HttpSession
	 */
	public static HttpSession getHttpSession() {
		final ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		final HttpSession session = (ra).getRequest().getSession();
		return session;
	}

	public static CLoggedUserRecord getLoggedUser() throws CSecurityException {
		return getLoggedUser(true);
	}

	public static CLoggedUserRecord getLoggedUser(final boolean throwException) throws CSecurityException {
		final Object loggedUser = getHttpSession().getAttribute(ISessionParametersKeys.SECURITY_LOGGED_USER);
		if (null != loggedUser) {
			return (CLoggedUserRecord) loggedUser;
		} else if (throwException) {
			throw new CSecurityException("USER_TIMEOUT");
		}
		return null;
	}

	/**
	 * Gets locale from the request context
	 * 
	 * @return actual locale
	 */
	public static Locale getLocale() {
		final ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return (ra).getRequest().getLocale();
	}
}
