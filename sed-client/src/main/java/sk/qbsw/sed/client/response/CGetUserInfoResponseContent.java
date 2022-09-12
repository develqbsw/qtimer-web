package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CPredefinedInteligentTimeStamp;

public class CGetUserInfoResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CLoggedUserRecord user;

	private CPredefinedInteligentTimeStamp predefinedInteligentTimeStamp;

	public CLoggedUserRecord getUser() {
		return user;
	}

	public void setUser(CLoggedUserRecord user) {
		this.user = user;
	}

	public CPredefinedInteligentTimeStamp getPredefinedInteligentTimeStamp() {
		return predefinedInteligentTimeStamp;
	}

	public void setPredefinedInteligentTimeStamp(CPredefinedInteligentTimeStamp predefinedInteligentTimeStamp) {
		this.predefinedInteligentTimeStamp = predefinedInteligentTimeStamp;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
