package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.CEmployeesStatusNew;

public class CBrwEmployeesStatusNewFetchResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CEmployeesStatusNew> result;

	public List<CEmployeesStatusNew> getResult() {
		return result;
	}

	public void setResult(List<CEmployeesStatusNew> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
