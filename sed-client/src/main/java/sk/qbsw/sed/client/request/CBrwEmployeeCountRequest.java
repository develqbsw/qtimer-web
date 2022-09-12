package sk.qbsw.sed.client.request;

public class CBrwEmployeeCountRequest extends ARequest {

	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected String name;

	public CBrwEmployeeCountRequest() {
		super();
	}

	public CBrwEmployeeCountRequest(String name) {
		super();
		this.name = name;
	}

	@Override
	public Boolean validate() {
		return true;
	}

	public String getCriteria() {
		return name;
	}

	public void setCriteria(String name) {
		this.name = name;
	}
}
