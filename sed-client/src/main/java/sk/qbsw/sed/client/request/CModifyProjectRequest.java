package sk.qbsw.sed.client.request;

import java.util.Date;

import sk.qbsw.sed.client.model.codelist.CProjectRecord;

public class CModifyProjectRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private CProjectRecord newRecord;

	private Date timestamp;

	public CModifyProjectRequest() {
		super();
	}

	public CModifyProjectRequest(Long id, CProjectRecord newRecord, Date timestamp) {
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

	public CProjectRecord getNewRecord() {
		return newRecord;
	}

	public void setNewRecord(CProjectRecord newRecord) {
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
