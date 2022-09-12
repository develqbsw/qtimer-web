package sk.qbsw.sed.client.request;

import com.google.gson.annotations.Expose;

public class CBrwLoadDataRequest extends ARequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	/** user login */
	@Expose
	protected Integer startRow;

	protected Integer endRow;

	protected String sortProperty;

	protected boolean sortAsc;

	public CBrwLoadDataRequest() {
		super();
	}

	public CBrwLoadDataRequest(int startRow, int endRow, String sortProperty, boolean sortAsc) {
		super();
		this.startRow = startRow;
		this.endRow = endRow;
		this.sortProperty = sortProperty;
		this.sortAsc = sortAsc;
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