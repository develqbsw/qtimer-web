package sk.qbsw.sed.client.request;

import com.google.gson.annotations.Expose;

/**
 * login data for requests
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.2.0
 */
public class CLoginDataForRequest extends CLoginRequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/** security token */
	@Expose
	protected String securityToken;

	/**
	 * @return the securityToken
	 */
	public String getSecurityToken() {
		return securityToken;
	}

	/**
	 * @param securityToken the securityToken to set
	 */
	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	@Override
	public Boolean validate() {
		if (securityToken != null) {
			if (login == null && password == null) {
				return true;
			}
			return false;
		} else {
			return super.validate();
		}
	}
}
