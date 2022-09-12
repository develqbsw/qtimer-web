package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.registration.CRegistrationClientRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationUserRecord;

public class CRegisterOrganizationRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CRegistrationClientRecord org;

	private CRegistrationUserRecord user;

	public CRegisterOrganizationRequest() {
		super();
	}

	public CRegisterOrganizationRequest(CRegistrationClientRecord org, CRegistrationUserRecord user) {
		super();
		this.org = org;
		this.user = user;
	}

	public CRegistrationClientRecord getOrg() {
		return org;
	}

	public void setOrg(CRegistrationClientRecord org) {
		this.org = org;
	}

	public CRegistrationUserRecord getUser() {
		return user;
	}

	public void setUser(CRegistrationUserRecord user) {
		this.user = user;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
