package sk.qbsw.sed.client.response;

import javax.validation.constraints.NotNull;

import com.google.gson.annotations.Expose;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;

/**
 * login response content
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.0.0
 */
public class CLoginResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/** security token */
	@Expose
	@NotNull
	private String securityToken;

	@Expose
	@NotNull
	private CLoggedUserRecord user;

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

	public CLoggedUserRecord getUser() {
		return user;
	}

	public void setUser(CLoggedUserRecord user) {
		this.user = user;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
