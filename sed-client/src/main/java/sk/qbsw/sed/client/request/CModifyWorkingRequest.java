package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.timestamp.CTimeStampAddRecord;

public class CModifyWorkingRequest extends ARequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	public CModifyWorkingRequest() {
		super();
	}

	public CModifyWorkingRequest(CTimeStampAddRecord record, Long id) {
		super();
		this.record = record;
		this.id = id;
	}

	private CTimeStampAddRecord record;

	private Long id;

	public CTimeStampAddRecord getRecord() {
		return record;
	}

	public void setRecord(CTimeStampAddRecord record) {
		this.record = record;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
