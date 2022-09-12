package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.codelist.CActivityBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;

public class CBrwActivityCountRequest extends ARequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CActivityBrwFilterCriteria criteria;

	public CBrwActivityCountRequest() {
		super();
	}

	public CBrwActivityCountRequest(IFilterCriteria criteria) {
		super();
		this.criteria = (CActivityBrwFilterCriteria) criteria;
	}

	@Override
	public Boolean validate() {
		return true;
	}

	public CActivityBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CActivityBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}
}