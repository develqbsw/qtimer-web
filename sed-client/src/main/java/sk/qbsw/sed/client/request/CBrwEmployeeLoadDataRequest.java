package sk.qbsw.sed.client.request;

public class CBrwEmployeeLoadDataRequest extends CBrwLoadDataRequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected String name;

	public CBrwEmployeeLoadDataRequest() {
		super();
	}

	public CBrwEmployeeLoadDataRequest(int startRow, int endRow, String sortProperty, boolean sortAsc, String name) {
		super(startRow, endRow, sortProperty, sortAsc);
		this.name = name;

	}

	public String getCriteria() {
		return name;
	}

	public void setCriteria(String name) {
		this.name = name;
	}
}
