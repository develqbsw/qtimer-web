package sk.qbsw.sed.client.request;

import com.google.gson.annotations.Expose;

/**
 * login request
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.0.0
 */
public class CAutoLoginRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/** user password */
	@Expose
	protected String autoLoginToken;

	protected String locale;

	public CAutoLoginRequest() {
		super();
	}

	public CAutoLoginRequest(String autoLoginToken, String locale) {
		super();
		this.autoLoginToken = autoLoginToken;
		this.locale = locale;
	}

	public String getAutoLoginToken() {
		return autoLoginToken;
	}

	public void setAutoLoginToken(String autoLoginToken) {
		this.autoLoginToken = autoLoginToken;
	}

	@Override
	public Boolean validate() {
		return true;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
}
