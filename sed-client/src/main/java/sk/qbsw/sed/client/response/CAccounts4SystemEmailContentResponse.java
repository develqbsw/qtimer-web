package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailRecord;

public class CAccounts4SystemEmailContentResponse extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CUserSystemEmailRecord> accounts4SystemEmail;

	public List<CUserSystemEmailRecord> getAccounts4SystemEmail() {
		return accounts4SystemEmail;
	}

	public void setAccounts4SystemEmail(List<CUserSystemEmailRecord> accounts4SystemEmail) {
		this.accounts4SystemEmail = accounts4SystemEmail;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
