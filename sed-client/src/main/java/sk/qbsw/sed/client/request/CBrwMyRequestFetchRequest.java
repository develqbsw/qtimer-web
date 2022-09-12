package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.request.CMyRequestsBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;

public class CBrwMyRequestFetchRequest extends CBrwLoadDataRequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CMyRequestsBrwFilterCriteria criteria;

	public CBrwMyRequestFetchRequest() {
		super();
	}

	public CBrwMyRequestFetchRequest(int startRow, int endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) {
		super(startRow, endRow, sortProperty, sortAsc);
		this.criteria = (CMyRequestsBrwFilterCriteria) criteria;
	}

	public CMyRequestsBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CMyRequestsBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
