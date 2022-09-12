package sk.qbsw.sed.api.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import sk.qbsw.sed.api.rest.service.CResponseService;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.response.EErrorCode;
import sk.qbsw.sed.framework.security.exception.CSecurityException;
import sk.qbsw.sed.server.ISessionParametersKeys;

/**
 * security filter pre sed api volania
 *
 * @author Podmajersky Lukas
 * @since 2.1.0
 * @version 2.3.0
 */
public class CSedSecurityFilter extends OncePerRequestFilter {
	private static CLoggedUserRecord getLoggedUser(HttpSession session) throws CSecurityException {
		final Object loggedUser = session.getAttribute(ISessionParametersKeys.SECURITY_LOGGED_USER);
		if (null != loggedUser) {
			return (CLoggedUserRecord) loggedUser;
		} else {
			throw new CSecurityException("USER_TIMEOUT");
		}
	}

	/** response service */
	@Autowired
	private CResponseService responseService;

	/** securer link */
	private final String[] securedUrls = new String[] { "api/rest/security/login", "api/rest/security/loginByAutoLoginToken", "api/rest/systemInfo/getVersion", 
			"api/rest/user/renewPassword", "api/rest/registration/register", "api/rest/legalForm/getValidRecords", "api/rest/user/getUserDetails", 
			"api/rest/request/approveRequestFromEmai", "api/rest/request/rejectRequestFromEmail", "/api/rest/photo/" };

	/** filter enabled flag */
	@Value("${api.security.filter.on}")
	private boolean filterEnabled;

	/**
	 * create security filter
	 */
	public CSedSecurityFilter() {
		super();
	}

	/**
	 * check for logged user
	 *
	 * @param request actual request
	 * @return error code if checking was not successful, or null if no error occurred
	 * @throws CSecurityException
	 */
	private EErrorCode checkForLogedUser(HttpSession session) {
		try {
			getLoggedUser(session);
		} catch (CSecurityException e) {
			return EErrorCode.USER_NOT_LOGGED;
		}

		return null;
	}

	/**
	 * filter
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		CSedRequestWrapper requestWrapper = new CSedRequestWrapper(request);
		// request.getSession().setMaxInactiveInterval(300); definujem vo web.xml
		String uri = requestWrapper.getRequestURI();

		if (filterEnabled && !isUrlSecure(uri)) {
			checkForLogedUser(request.getSession());
		}

		filterChain.doFilter(requestWrapper, response);
	}

	/**
	 * @return the filterEnabled
	 */
	public boolean isFilterEnabled() {
		return filterEnabled;
	}

	/**
	 * test of url
	 *
	 * @param url checked url
	 * @return true if url is secured and has to be checked, false otherwise
	 */
	private boolean isUrlSecure(String url) {
		for (String securedUrl : securedUrls) {
			if (url.contains(securedUrl)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param filterEnabled the filterEnabled to set
	 */
	public void setFilterEnabled(boolean filterEnabled) {
		this.filterEnabled = filterEnabled;
	}
}
