package sk.qbsw.sed.client.request;

import com.google.gson.annotations.Expose;

import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.CMyTimesheetGenerateBrwFilterCriteria;

public class CBrwMyTimeStampGenerateFetchRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/** user login */
	@Expose
	protected Integer startRow;

	protected Integer endRow;

	protected String sortProperty;
	protected boolean sortAsc;

	protected CMyTimesheetGenerateBrwFilterCriteria criteria;

	public CBrwMyTimeStampGenerateFetchRequest() {
		super();
	}

	public CBrwMyTimeStampGenerateFetchRequest(int startRow, int endRow, String sortProperty, boolean sortAsc, IFilterCriteria criteria) {
		super();
		this.startRow = startRow;
		this.endRow = endRow;
		this.sortProperty = sortProperty;
		this.sortAsc = sortAsc;
		this.criteria = (CMyTimesheetGenerateBrwFilterCriteria) criteria;
	}

	public Integer getStartRow() {
		return startRow;
	}

	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	public Integer getEndRow() {
		return endRow;
	}

	public void setEndRow(Integer endRow) {
		this.endRow = endRow;
	}

	public CMyTimesheetGenerateBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CMyTimesheetGenerateBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}

	public String getSortProperty() {
		return sortProperty;
	}

	public void setSortProperty(String sortProperty) {
		this.sortProperty = sortProperty;
	}

	public boolean isSortAsc() {
		return sortAsc;
	}

	public void setSortAsc(boolean sortAsc) {
		this.sortAsc = sortAsc;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
