package sk.qbsw.sed.client.request;

import com.google.gson.annotations.Expose;

import sk.qbsw.sed.client.model.detail.CUserDetailRecord;

/**
 * logout request
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.0.0
 */
public class CAddUserRequest extends CLoginDataForRequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	@Expose
	protected CUserDetailRecord userToAdd;

	public CAddUserRequest() {
		super();
	}

	public CAddUserRequest(CUserDetailRecord userToAdd, String securityToken) {
		super();
		this.userToAdd = userToAdd;
		this.securityToken = securityToken;
	}

	public CUserDetailRecord getUserToAdd() {
		return userToAdd;
	}

	public void setUserToAdd(CUserDetailRecord userToAdd) {
		this.userToAdd = userToAdd;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
