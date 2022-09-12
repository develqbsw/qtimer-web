package sk.qbsw.sed.model;

import java.io.Serializable;

import org.apache.http.client.CookieStore;

import sk.qbsw.sed.client.model.security.CLoggedUserRecord;

public class CLoggedUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private final CLoggedUserRecord record;
	private final String token;
	private final CookieStore cookies;

	public CLoggedUser(CLoggedUserRecord record, String token, CookieStore cookies) {
		super();
		this.record = record;
		this.token = token;
		this.cookies = cookies;
	}

	public CLoggedUserRecord getRecord() {
		return record;
	}

	public String getToken() {
		return token;
	}

	public CookieStore getCookies() {
		return cookies;
	}
}
