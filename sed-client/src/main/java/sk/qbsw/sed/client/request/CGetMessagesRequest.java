package sk.qbsw.sed.client.request;

import java.io.Serializable;
import java.util.Locale;

public class CGetMessagesRequest extends ARequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Locale locale;

	public Locale getLocale() {
		return locale;
	}

	public CGetMessagesRequest(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
