package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;

public class CLongResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long value;

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
