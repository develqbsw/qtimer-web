package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CPasswordChangeRecord implements Serializable {

	String name;
	String login;
	String originalPwd;
	String newPwd;
	String newPwd2;

	public CPasswordChangeRecord(String name, String login) {
		this.name = name;
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getNewPwd2() {
		return newPwd2;
	}

	public void setNewPwd2(String newPwd2) {
		this.newPwd2 = newPwd2;
	}
}
