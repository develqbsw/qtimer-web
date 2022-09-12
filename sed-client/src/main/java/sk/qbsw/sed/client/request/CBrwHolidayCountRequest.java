package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.codelist.CHolidayBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;

public class CBrwHolidayCountRequest extends ARequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CHolidayBrwFilterCriteria criteria;

	public CBrwHolidayCountRequest() {
		super();
	}

	public CBrwHolidayCountRequest(IFilterCriteria criteria) {
		super();
		this.criteria = (CHolidayBrwFilterCriteria) criteria;
	}

	@Override
	public Boolean validate() {
		return true;
	}

	public CHolidayBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CHolidayBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}
}