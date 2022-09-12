package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;

public class CTimeStampRecordResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private CTimeStampRecord timeStampRecord;

	public CTimeStampRecord getTimeStampRecord() {
		return timeStampRecord;
	}

	public void setTimeStampRecord(CTimeStampRecord timeStampRecord) {
		this.timeStampRecord = timeStampRecord;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
