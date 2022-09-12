package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.timestamp.CTimeStampAddRecord;

public class CStopNonWorkingRequest extends ARequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	public CStopNonWorkingRequest() {
		super();
	}

	public CStopNonWorkingRequest(CTimeStampAddRecord record, boolean continueWork) {
		super();
		this.record = record;
		this.continueWork = continueWork;
	}

	private CTimeStampAddRecord record;

	private boolean continueWork;

	public CTimeStampAddRecord getRecord() {
		return record;
	}

	public void setRecord(CTimeStampAddRecord record) {
		this.record = record;
	}

	public boolean isContinueWork() {
		return continueWork;
	}

	public void setContinueWork(boolean continueWork) {
		this.continueWork = continueWork;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
