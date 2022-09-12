package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.codelist.CJiraTokenGenerationRecord;

public class CJiraTokenGenerationRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2970660465984427282L;

	private CJiraTokenGenerationRecord record;

	public CJiraTokenGenerationRequest() {
		super();
	}
	
	public CJiraTokenGenerationRequest(CJiraTokenGenerationRecord record) {
		super();
		this.record = record;
	}

	public CJiraTokenGenerationRecord getRecord() {
		return record;
	}

	public void setRecord(CJiraTokenGenerationRecord record) {
		this.record = record;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
