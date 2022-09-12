package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.codelist.CJiraTokenGenerationRecord;

public class CJiraTokenGenerationResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3084833957840445617L;
	
	private CJiraTokenGenerationRecord record;

	public CJiraTokenGenerationRecord getRecord() {
		return record;
	}

	public void setRecord(CJiraTokenGenerationRecord record) {
		this.record = record;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
