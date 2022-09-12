package sk.qbsw.sed.fw;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.request.Request;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.utils.CAuthenticatedSession;

/**
 * 
 * @author Peter Božík
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class ASession extends CAuthenticatedSession {
	private static final long serialVersionUID = 1L;

	public ASession(Request request) {
		super(request);
	}

	public String getLoggedUserName() {
		CLoggedUserRecord user = getUser();
		if (user == null) {
			return "";
		} else {
			return (StringUtils.isNotEmpty(user.getName()) ? user.getName() : "") + " " + (StringUtils.isNotEmpty(user.getSurname()) ? user.getSurname() : "");
		}
	}
}
