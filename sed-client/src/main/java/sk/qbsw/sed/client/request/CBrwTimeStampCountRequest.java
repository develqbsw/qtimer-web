package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CMyTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;

public class CBrwTimeStampCountRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CSubrodinateTimeStampBrwFilterCriteria criteria;

	public CBrwTimeStampCountRequest() {
		super();
	}

	public CBrwTimeStampCountRequest(IFilterCriteria criteria) {
		super();
		this.criteria = (CSubrodinateTimeStampBrwFilterCriteria) criteria;
	}

	public CMyTimeStampBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CSubrodinateTimeStampBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
