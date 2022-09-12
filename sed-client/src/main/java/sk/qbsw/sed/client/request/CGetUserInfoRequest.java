package sk.qbsw.sed.client.request;

public class CGetUserInfoRequest extends CLoginDataForRequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private String pin;

	public CGetUserInfoRequest() {
		super();
	}

	public CGetUserInfoRequest(String pin, String securityToken) {
		super();
		this.pin = pin;
		this.securityToken = securityToken;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
