package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;

public class CBrwTimeStampFetchRequest extends CBrwLoadDataRequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CSubrodinateTimeStampBrwFilterCriteria criteria;

	public CBrwTimeStampFetchRequest() {
		super();
	}

	public CBrwTimeStampFetchRequest(int startRow, int endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) {
		super(startRow, endRow, sortProperty, sortAsc);
		this.criteria = (CSubrodinateTimeStampBrwFilterCriteria) criteria;
	}

	public CSubrodinateTimeStampBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CSubrodinateTimeStampBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}
}
