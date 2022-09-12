package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.restriction.CGroupsAIData;

public class CBrwGroupsAIFetchResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CGroupsAIData> result;

	public List<CGroupsAIData> getResult() {
		return result;
	}

	public void setResult(List<CGroupsAIData> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
