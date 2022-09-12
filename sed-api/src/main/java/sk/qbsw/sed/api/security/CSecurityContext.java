package sk.qbsw.sed.api.security;

import javax.servlet.http.HttpSession;

/**
 * security context for api calls
 *
 */
public class CSecurityContext {
	/** stored security token key */
	private static final String STORED_SECURITY_TOKEN = "sed_security_token";

	/** stored login key */
	private static final String STORED_LOGIN = "sed_login";

	private CSecurityContext() {
		// Auto-generated constructor stub
	}

	/**
	 * add security token for login in session
	 *
	 * @param securityToken security token
	 * @param login         login for security token
	 * @param session       actual session
	 */
	public static void addSecurityToken(String securityToken, String login, HttpSession session) {
		session.setAttribute(STORED_LOGIN, login);
		session.setAttribute(STORED_SECURITY_TOKEN, securityToken);
	}

	/**
	 * invalidate security token for login
	 *
	 * @param login   login
	 * @param session actual session
	 */
	public static void invalidateSecurityToken(String login, HttpSession session) {
		String storedLogin = (String) session.getAttribute(STORED_LOGIN);
		if (login.equals(storedLogin)) {
			session.invalidate();
		}
	}

	/**
	 * check for security token validity
	 *
	 * @param securityToken security token to check
	 * @param session       actual session
	 * @return true if security token is valid, false otherwise
	 */
	public static boolean isSecurityTokenValid(String securityToken, HttpSession session) {
		String storedSecurityToken = (String) session.getAttribute(STORED_SECURITY_TOKEN);
		return securityToken.equals(storedSecurityToken);
	}
}
