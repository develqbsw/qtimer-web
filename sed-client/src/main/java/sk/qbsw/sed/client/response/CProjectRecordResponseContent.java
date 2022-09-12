package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.codelist.CProjectRecord;

public class CProjectRecordResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CProjectRecord projectRecord;

	public CProjectRecord getProjectRecord() {
		return projectRecord;
	}

	public void setProjectRecord(CProjectRecord projectRecord) {
		this.projectRecord = projectRecord;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
