package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CJiraTokenGenerationRecord implements Serializable {

	private String jiraLink;
	private String verificationCode;
	private String token;
	private String secret;

	public CJiraTokenGenerationRecord() {

	}

	public CJiraTokenGenerationRecord(String jiraLink, String verificationCode, String token, String secret) {
		this.verificationCode = verificationCode;
		this.token = token;
		this.secret = secret;
		this.jiraLink = jiraLink;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getJiraLink() {
		return jiraLink;
	}

	public void setJiraLink(String jiraLink) {
		this.jiraLink = jiraLink;
	}

}
