package sk.qbsw.sed.client.response;

import java.util.Map;

import sk.qbsw.sed.client.exception.CDataValidationException;

public class CGetLastProjectToActivityRelationMapResponseContent extends AResponseContent {

	private static final long serialVersionUID = 1L;

	private Map<Long, Long> result;

	public Map<Long, Long> getResult() {
		return result;
	}

	public void setResult(Map<Long, Long> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
