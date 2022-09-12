package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.codelist.CHolidayRecord;

public class CHolidayRecordListResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<CHolidayRecord> list;

	public List<CHolidayRecord> getList() {
		return list;
	}

	public void setList(List<CHolidayRecord> list) {
		this.list = list;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
