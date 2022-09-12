package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.detail.CClientDetailRecord;

public class CClientDetailRecordResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CClientDetailRecord record;

	public CClientDetailRecord getRecord() {
		return record;
	}

	public void setRecord(CClientDetailRecord record) {
		this.record = record;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
