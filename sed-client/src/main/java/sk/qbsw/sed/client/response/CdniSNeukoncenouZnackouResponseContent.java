package sk.qbsw.sed.client.response;

import java.util.Calendar;
import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;

public class CdniSNeukoncenouZnackouResponseContent extends AResponseContent {

	private List<Calendar> dniSNeukoncenouZnackou;

	public List<Calendar> getDniSNeukoncenouZnackou() {
		return dniSNeukoncenouZnackou;
	}

	public void setDniSNeukoncenouZnackou(List<Calendar> dniSNeukoncenouZnackou) {
		this.dniSNeukoncenouZnackou = dniSNeukoncenouZnackou;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
