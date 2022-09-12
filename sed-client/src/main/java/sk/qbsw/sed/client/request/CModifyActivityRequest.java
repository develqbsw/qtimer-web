package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.codelist.CActivityRecord;

public class CModifyActivityRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private CActivityRecord newRecord;

	public CModifyActivityRequest() {
		super();
	}

	public CModifyActivityRequest(Long id, CActivityRecord newRecord) {
		super();
		this.id = id;
		this.newRecord = newRecord;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CActivityRecord getNewRecord() {
		return newRecord;
	}

	public void setNewRecord(CActivityRecord newRecord) {
		this.newRecord = newRecord;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
