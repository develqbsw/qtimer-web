package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.codelist.CHolidayRecord;

public class CModifyHolidayRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private CHolidayRecord newRecord;

	public CModifyHolidayRequest() {
		super();
	}

	public CModifyHolidayRequest(Long id, CHolidayRecord newRecord) {
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

	public CHolidayRecord getNewRecord() {
		return newRecord;
	}

	public void setNewRecord(CHolidayRecord newRecord) {
		this.newRecord = newRecord;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
