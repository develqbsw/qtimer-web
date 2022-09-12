package sk.qbsw.sed.client.response;

import java.util.Date;

import sk.qbsw.sed.client.exception.CDataValidationException;

public class CDateResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
