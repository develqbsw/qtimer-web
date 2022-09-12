package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;

public class CLoggedUserRecordListResponseContent extends AResponseContent {

	private static final long serialVersionUID = 1L;

	private List<CLoggedUserRecord> users;

	public List<CLoggedUserRecord> getUsers() {
		return users;
	}

	public void setUsers(List<CLoggedUserRecord> users) {
		this.users = users;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
