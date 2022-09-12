package sk.qbsw.sed.client.request;

public class CChangePasswordRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private String login;

	private String originalPwd;

	private String newPwd;

	public CChangePasswordRequest() {
		super();
	}

	public CChangePasswordRequest(String login, String originalPwd, String newPwd) {
		super();
		this.login = login;
		this.originalPwd = originalPwd;
		this.newPwd = newPwd;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getOriginalPwd() {
		return originalPwd;
	}

	public void setOriginalPwd(String originalPwd) {
		this.originalPwd = originalPwd;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
