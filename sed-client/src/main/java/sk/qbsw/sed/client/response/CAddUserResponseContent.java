package sk.qbsw.sed.client.response;

import javax.validation.constraints.NotNull;

import com.google.gson.annotations.Expose;

import sk.qbsw.sed.client.exception.CDataValidationException;

public class CAddUserResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/** security token */
	@Expose
	@NotNull
	private Long userId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
