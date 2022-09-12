package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;

public class CMultipleActivityResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Long> usersSuccessful;

	private List<Long> usersFail;

	public List<Long> getUsersSuccessful() {
		return usersSuccessful;
	}

	public void setUsersSuccessful(List<Long> usersSuccessful) {
		this.usersSuccessful = usersSuccessful;
	}

	public List<Long> getUsersFail() {
		return usersFail;
	}

	public void setUsersFail(List<Long> usersFail) {
		this.usersFail = usersFail;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
