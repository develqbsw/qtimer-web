package sk.qbsw.sed.client.request;

import java.util.Date;

import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;

public class CSplitTimestampRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	CTimeStampRecord record;
	Date splitTime;

	public CSplitTimestampRequest() {
		super();
	}

	public CSplitTimestampRequest(CTimeStampRecord record, Date splitTime) {
		super();
		this.record = record;
		this.splitTime = splitTime;
	}

	public CTimeStampRecord getRecord() {
		return record;
	}

	public void setRecordId(CTimeStampRecord record) {
		this.record = record;
	}

	public Date getSplitTime() {
		return splitTime;
	}

	public void setSplitTime(Date splitTime) {
		this.splitTime = splitTime;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
