package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CTimeStampGenerateFilterCriteria;

public class CGenerateBrwTimeStampFetchRequest extends CBrwLoadDataRequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CTimeStampGenerateFilterCriteria criteria;

	public CGenerateBrwTimeStampFetchRequest() {
		super();
	}

	public CGenerateBrwTimeStampFetchRequest(int startRow, int endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) {
		super(startRow, endRow, sortProperty, sortAsc);
		this.criteria = (CTimeStampGenerateFilterCriteria) criteria;
	}

	public CTimeStampGenerateFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CTimeStampGenerateFilterCriteria criteria) {
		this.criteria = criteria;
	}
}
