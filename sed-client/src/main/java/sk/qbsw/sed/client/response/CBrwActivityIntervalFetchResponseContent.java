package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;

public class CBrwActivityIntervalFetchResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CActivityIntervalData> result;

	public List<CActivityIntervalData> getResult() {
		return result;
	}

	public void setResult(List<CActivityIntervalData> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
