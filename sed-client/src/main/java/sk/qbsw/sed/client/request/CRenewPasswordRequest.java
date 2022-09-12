package sk.qbsw.sed.client.request;

public class CRenewPasswordRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private String login;

	private String email;

	public CRenewPasswordRequest() {
		super();
	}

	public CRenewPasswordRequest(String login, String email) {
		super();
		this.login = login;
		this.email = email;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
