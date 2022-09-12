package sk.qbsw.sed.web.ui;

import java.util.Locale;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.request.Request;

import sk.qbsw.sed.fw.ASession;

public class CSedSession extends ASession {
	private boolean flagFirstTime = true;

	private static final long serialVersionUID = 1L;

	public CSedSession(Request request) {
		super(request);
	}

	/**
	 * Gets actual session.
	 *
	 * @return active session
	 */
	public static CSedSession get() {
		return (CSedSession) AuthenticatedWebSession.get();
	}

	public void setSlovakLocale() {
		if (flagFirstTime) {
			// default jazyk pri logine nastavíme na sk a nie podľa prehliadača
			setLocale(new Locale("sk", "SK"));
			flagFirstTime = false;
		}
	}
}
