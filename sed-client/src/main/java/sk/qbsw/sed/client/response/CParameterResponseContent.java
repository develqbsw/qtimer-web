package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.params.CParameter;

public class CParameterResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CParameter parameter;

	public CParameter getParameter() {
		return parameter;
	}

	public void setParameter(CParameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
