package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CEmployeesStatusBrwFilterCriteria;

public class CBrwEmployeesStatusFetchRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CEmployeesStatusBrwFilterCriteria criteria;

	public CBrwEmployeesStatusFetchRequest() {
		super();
	}

	public CBrwEmployeesStatusFetchRequest(int startRow, int endRow, IFilterCriteria criteria) {
		super();
		this.criteria = (CEmployeesStatusBrwFilterCriteria) criteria;
	}

	public CEmployeesStatusBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CEmployeesStatusBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
