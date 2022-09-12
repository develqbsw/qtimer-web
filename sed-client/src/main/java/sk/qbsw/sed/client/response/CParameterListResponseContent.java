package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.params.CParameter;

public class CParameterListResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<CParameter> list;

	public List<CParameter> getList() {
		return list;
	}

	public void setList(List<CParameter> list) {
		this.list = list;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
