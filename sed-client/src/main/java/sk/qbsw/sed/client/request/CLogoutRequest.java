package sk.qbsw.sed.client.request;

import com.google.gson.annotations.Expose;

/**
 * logout request
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.0.0
 */
public class CLogoutRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/** user login */
	@Expose
	protected String login;

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
