package sk.qbsw.sed.communication.model;

import java.io.Serializable;

import org.apache.http.client.CookieStore;

public class CInvokerInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String authToken;
	private CookieStore cookies;

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public CookieStore getCookies() {
		return cookies;
	}

	public void setCookies(CookieStore cookies) {
		this.cookies = cookies;
	}
}
