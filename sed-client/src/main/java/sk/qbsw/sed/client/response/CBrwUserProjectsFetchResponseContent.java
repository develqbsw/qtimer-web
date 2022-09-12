package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.brw.CUserProjectRecord;

public class CBrwUserProjectsFetchResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CUserProjectRecord> result;

	public List<CUserProjectRecord> getResult() {
		return result;
	}

	public void setResult(List<CUserProjectRecord> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
