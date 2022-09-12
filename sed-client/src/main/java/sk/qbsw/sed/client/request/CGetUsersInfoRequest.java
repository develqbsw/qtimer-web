package sk.qbsw.sed.client.request;

import java.util.List;

public class CGetUsersInfoRequest extends CLoginDataForRequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<String> pins;

	public CGetUsersInfoRequest() {
		super();
	}

	public CGetUsersInfoRequest(List<String> pins, String securityToken) {
		super();
		this.pins = pins;
		this.securityToken = securityToken;
	}

	public List<String> getPins() {
		return pins;
	}

	public void setPins(List<String> pins) {
		this.pins = pins;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
