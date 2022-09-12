package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CPredefinedInteligentTimeStamp;

public class CGetUsersInfoResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<CLoggedUserRecord> users;

	private CPredefinedInteligentTimeStamp predefinedInteligentTimeStamp;

	public List<CLoggedUserRecord> getUsers() {
		return users;
	}

	public void setUsers(List<CLoggedUserRecord> users) {
		this.users = users;
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
