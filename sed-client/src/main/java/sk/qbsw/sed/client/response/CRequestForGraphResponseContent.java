package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.request.CRequestRecordForGraph;

public class CRequestForGraphResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CRequestRecordForGraph> result;

	public List<CRequestRecordForGraph> getResult() {
		return result;
	}

	public void setResult(List<CRequestRecordForGraph> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
