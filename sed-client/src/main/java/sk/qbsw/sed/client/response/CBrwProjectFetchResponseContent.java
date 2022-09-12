package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.codelist.CProjectRecord;

public class CBrwProjectFetchResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CProjectRecord> result;

	public List<CProjectRecord> getResult() {
		return result;
	}

	public void setResult(List<CProjectRecord> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
