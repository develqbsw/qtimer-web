package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.codelist.CActivityBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;

public class CBrwActivityFetchRequest extends CBrwLoadDataRequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CActivityBrwFilterCriteria criteria;

	public CBrwActivityFetchRequest() {
		super();
	}

	public CBrwActivityFetchRequest(int startRow, int endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) {
		super(startRow, endRow, sortProperty, sortAsc);
		this.criteria = (CActivityBrwFilterCriteria) criteria;

	}

	public CActivityBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CActivityBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}
}
