package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;

public class CBrwTimeStampFetchResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CTimeStampRecord> result;

	public List<CTimeStampRecord> getResult() {
		return result;
	}

	public void setResult(List<CTimeStampRecord> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
