package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.request.CRequestRecord;

/**
 * logout request
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.0.0
 */
public class CAddRequestRequest extends CLoginDataForRequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Long type;

	private CRequestRecord model;

	private boolean ignoreDuplicity;

	public CAddRequestRequest() {
		super();
	}

	public CAddRequestRequest(Long type, CRequestRecord model, boolean ignoreDuplicity) {
		super();
		this.type = type;
		this.model = model;
		this.ignoreDuplicity = ignoreDuplicity;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public CRequestRecord getModel() {
		return model;
	}

	public void setModel(CRequestRecord model) {
		this.model = model;
	}

	public boolean isIgnoreDuplicity() {
		return ignoreDuplicity;
	}

	public void setIgnoreDuplicity(boolean ignoreDuplicity) {
		this.ignoreDuplicity = ignoreDuplicity;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
