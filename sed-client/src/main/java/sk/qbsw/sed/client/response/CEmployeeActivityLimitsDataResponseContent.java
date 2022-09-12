package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.restriction.CEmployeeActivityLimitsData;

public class CEmployeeActivityLimitsDataResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CEmployeeActivityLimitsData employeeActivityLimitsData;

	public CEmployeeActivityLimitsData getEmployeeActivityLimitsData() {
		return employeeActivityLimitsData;
	}

	public void setEmployeeActivityLimitsData(CEmployeeActivityLimitsData employeeActivityLimitsData) {
		this.employeeActivityLimitsData = employeeActivityLimitsData;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
