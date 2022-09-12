package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.codelist.CProjectBrwFilterCriteria;
import sk.qbsw.sed.client.model.table.IFilterCriteria;

public class CBrwProjectCountRequest extends ARequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CProjectBrwFilterCriteria criteria;

	public CBrwProjectCountRequest() {
		super();
	}

	public CBrwProjectCountRequest(IFilterCriteria criteria) {
		super();
		this.criteria = (CProjectBrwFilterCriteria) criteria;
	}

	@Override
	public Boolean validate() {
		return true;
	}

	public CProjectBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CProjectBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}
}