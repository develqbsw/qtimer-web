package sk.qbsw.sed.client.request;

import java.util.Date;

import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;

public class CModifyTimesheetRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected Long id;

	protected CTimeStampRecord newRecord;

	protected Date timestamp;

	public CModifyTimesheetRequest() {
		super();
	}

	public CModifyTimesheetRequest(Long id, CTimeStampRecord newRecord, Date timestamp) {
		super();
		this.id = id;
		this.newRecord = newRecord;
		this.timestamp = timestamp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CTimeStampRecord getNewRecord() {
		return newRecord;
	}

	public void setNewRecord(CTimeStampRecord newRecord) {
		this.newRecord = newRecord;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
