package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.request.CRequestRecord;

public class CBrwRequestFetchResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CRequestRecord> result;

	public List<CRequestRecord> getResult() {
		return result;
	}

	public void setResult(List<CRequestRecord> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
