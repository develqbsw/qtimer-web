package sk.qbsw.sed.client.request;

public class CChangePinRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private String login;

	private String newPin;

	public CChangePinRequest(String login, String newPin) {
		super();
		this.login = login;
		this.newPin = newPin;
	}

	public CChangePinRequest() {
		super();
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNewPin() {
		return newPin;
	}

	public void setNewPin(String newPin) {
		this.newPin = newPin;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
