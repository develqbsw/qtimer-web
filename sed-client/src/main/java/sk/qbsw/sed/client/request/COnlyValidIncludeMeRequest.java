package sk.qbsw.sed.client.request;

/**
 */
public class COnlyValidIncludeMeRequest extends CLoginDataForRequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Boolean onlyValid;
	private Boolean includeMe;

	public COnlyValidIncludeMeRequest() {
		super();
	}

	public COnlyValidIncludeMeRequest(Boolean onlyValid, Boolean includeMe) {
		super();
		this.onlyValid = onlyValid;
		this.includeMe = includeMe;
	}

	public Boolean getOnlyValid() {
		return onlyValid;
	}

	public void setOnlyValid(Boolean onlyValid) {
		this.onlyValid = onlyValid;
	}

	public Boolean getIncludeMe() {
		return includeMe;
	}

	public void setIncludeMe(Boolean includeMe) {
		this.includeMe = includeMe;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
