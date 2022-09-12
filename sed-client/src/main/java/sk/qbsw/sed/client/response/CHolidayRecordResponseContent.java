package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.codelist.CHolidayRecord;

public class CHolidayRecordResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	CHolidayRecord holidayRecord;

	public CHolidayRecord getHolidayRecord() {
		return holidayRecord;
	}

	public void setHolidayRecord(CHolidayRecord holidayRecord) {
		this.holidayRecord = holidayRecord;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
