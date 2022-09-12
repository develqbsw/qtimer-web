package sk.qbsw.sed.client.request;

public class CGetGeneratedPINRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private String login;

	private String oldPINvalue;

	public CGetGeneratedPINRequest() {
		super();
	}

	public CGetGeneratedPINRequest(String login, String oldPINvalue) {
		super();
		this.login = login;
		this.oldPINvalue = oldPINvalue;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getOldPINvalue() {
		return oldPINvalue;
	}

	public void setOldPINvalue(String oldPINvalue) {
		this.oldPINvalue = oldPINvalue;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
