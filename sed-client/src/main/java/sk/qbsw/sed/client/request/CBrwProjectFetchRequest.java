package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.codelist.CProjectBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;

public class CBrwProjectFetchRequest extends CBrwLoadDataRequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CProjectBrwFilterCriteria criteria;

	public CBrwProjectFetchRequest() {
		super();
	}

	public CBrwProjectFetchRequest(int startRow, int endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) {
		super(startRow, endRow, sortProperty, sortAsc);
		this.criteria = (CProjectBrwFilterCriteria) criteria;

	}

	public CProjectBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CProjectBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}
}
