package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.request.CSubordinateRequestsBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;

public class CBrwSubordinateRequestCountRequest extends ARequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CSubordinateRequestsBrwFilterCriteria criteria;

	public CBrwSubordinateRequestCountRequest() {
		super();
	}

	public CBrwSubordinateRequestCountRequest(IFilterCriteria criteria) {
		super();
		this.criteria = (CSubordinateRequestsBrwFilterCriteria) criteria;
	}

	@Override
	public Boolean validate() {
		return true;
	}

	public CSubordinateRequestsBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CSubordinateRequestsBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}
}
