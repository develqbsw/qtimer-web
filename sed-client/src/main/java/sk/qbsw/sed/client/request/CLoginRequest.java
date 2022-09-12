package sk.qbsw.sed.client.request;

import com.google.gson.annotations.Expose;

/**
 * login request
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.0.0
 */
public class CLoginRequest extends CLogoutRequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/** user password */
	@Expose
	protected String password;

	protected Boolean staySignedIn;

	protected String locale;

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Boolean validate() {
		return true;
	}

	public Boolean getStaySignedIn() {
		return staySignedIn;
	}

	public void setStaySignedIn(Boolean staySignedIn) {
		this.staySignedIn = staySignedIn;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
}
