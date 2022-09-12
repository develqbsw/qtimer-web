package sk.qbsw.sed.client.response;

import javax.validation.constraints.NotNull;

import com.google.gson.annotations.Expose;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;

public class CGetUserDetailsResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/** security token */
	@Expose
	@NotNull
	private CUserDetailRecord user;

	public CUserDetailRecord getUser() {
		return user;
	}

	public void setUser(CUserDetailRecord user) {
		this.user = user;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
