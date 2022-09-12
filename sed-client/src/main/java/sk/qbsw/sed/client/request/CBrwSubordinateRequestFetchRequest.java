package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.request.CSubordinateRequestsBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;

public class CBrwSubordinateRequestFetchRequest extends CBrwLoadDataRequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CSubordinateRequestsBrwFilterCriteria criteria;

	public CBrwSubordinateRequestFetchRequest() {
		super();
	}

	public CBrwSubordinateRequestFetchRequest(int startRow, int endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) {
		super(startRow, endRow, sortProperty, sortAsc);
		this.criteria = (CSubordinateRequestsBrwFilterCriteria) criteria;
	}

	public CSubordinateRequestsBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CSubordinateRequestsBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
