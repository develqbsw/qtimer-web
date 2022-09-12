package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;

public class CLoggedUserRecordResponseContent extends AResponseContent {

	private static final long serialVersionUID = 1L;

	private CLoggedUserRecord user;

	public CLoggedUserRecord getUser() {
		return user;
	}

	public void setUser(CLoggedUserRecord user) {
		this.user = user;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
