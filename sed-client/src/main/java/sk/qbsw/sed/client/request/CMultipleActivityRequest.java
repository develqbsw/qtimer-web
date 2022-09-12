package sk.qbsw.sed.client.request;

import java.util.List;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;

public class CMultipleActivityRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<CLoggedUserRecord> users;

	private String activity;

	public CMultipleActivityRequest() {
		super();
	}

	public CMultipleActivityRequest(List<CLoggedUserRecord> users, String activity) {
		super();
		this.users = users;
		this.activity = activity;
	}

	public List<CLoggedUserRecord> getUsers() {
		return users;
	}

	public void setUsers(List<CLoggedUserRecord> users) {
		this.users = users;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
