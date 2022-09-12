package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.brw.CEmployeesStatus;

public class CBrwEmployeesStatusFetchResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CEmployeesStatus> result;

	public List<CEmployeesStatus> getResult() {
		return result;
	}

	public void setResult(List<CEmployeesStatus> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
