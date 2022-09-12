package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;

public class CBrwCountResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Long count;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
