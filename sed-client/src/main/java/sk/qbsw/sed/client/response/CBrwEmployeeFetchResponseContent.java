package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;

public class CBrwEmployeeFetchResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CUserDetailRecord> result;

	public List<CUserDetailRecord> getResult() {
		return result;
	}

	public void setResult(List<CUserDetailRecord> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
