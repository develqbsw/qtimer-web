package sk.qbsw.sed.model;

import java.io.Serializable;

public class CLoginModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private boolean staySignedIn;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isStaySignedIn() {
		return staySignedIn;
	}

	public void setStaySignedIn(boolean staySignedIn) {
		this.staySignedIn = staySignedIn;
	}
}
