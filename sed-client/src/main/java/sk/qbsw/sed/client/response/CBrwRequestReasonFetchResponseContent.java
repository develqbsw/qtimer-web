package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.codelist.CRequestReasonRecord;

public class CBrwRequestReasonFetchResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CRequestReasonRecord> result;

	public List<CRequestReasonRecord> getResult() {
		return result;
	}

	public void setResult(List<CRequestReasonRecord> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
