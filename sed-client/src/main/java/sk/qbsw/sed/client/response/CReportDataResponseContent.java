package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.framework.report.model.CComplexInputReportModel;

public class CReportDataResponseContent extends AResponseContent {

	private static final long serialVersionUID = 1L;

	private CComplexInputReportModel model;

	public CComplexInputReportModel getModel() {
		return model;
	}

	public void setModel(CComplexInputReportModel model) {
		this.model = model;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
