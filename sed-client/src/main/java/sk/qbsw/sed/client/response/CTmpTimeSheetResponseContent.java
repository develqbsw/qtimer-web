package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;

public class CTmpTimeSheetResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private CTmpTimeSheet tmpTimeSheet;

	public CTmpTimeSheet getTmpTimeSheet() {
		return tmpTimeSheet;
	}

	public void setTmpTimeSheet(CTmpTimeSheet tmpTimeSheet) {
		this.tmpTimeSheet = tmpTimeSheet;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
