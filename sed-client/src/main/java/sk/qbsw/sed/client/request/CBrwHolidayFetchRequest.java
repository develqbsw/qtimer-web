package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.codelist.CHolidayBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;

public class CBrwHolidayFetchRequest extends CBrwLoadDataRequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CHolidayBrwFilterCriteria criteria;

	public CBrwHolidayFetchRequest() {
		super();
	}

	public CBrwHolidayFetchRequest(int startRow, int endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) {
		super(startRow, endRow, sortProperty, sortAsc);
		this.criteria = (CHolidayBrwFilterCriteria) criteria;
	}

	public CHolidayBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CHolidayBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}
}