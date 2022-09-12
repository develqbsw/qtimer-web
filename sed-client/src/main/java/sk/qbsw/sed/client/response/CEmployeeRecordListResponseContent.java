package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;

public class CEmployeeRecordListResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CEmployeeRecord> employeeRecordList;

	public List<CEmployeeRecord> getEmployeeRecordList() {
		return employeeRecordList;
	}

	public void setEmployeeRecordList(List<CEmployeeRecord> employeeRecordList) {
		this.employeeRecordList = employeeRecordList;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
