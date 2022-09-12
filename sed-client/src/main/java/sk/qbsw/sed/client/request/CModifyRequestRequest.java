package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.request.CRequestRecord;

/**
 * logout request
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.0.0
 */
public class CModifyRequestRequest extends CLoginDataForRequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Long id;

	private CRequestRecord model;

	private boolean ignoreDuplicity;

	public CModifyRequestRequest() {
		super();
	}

	public CModifyRequestRequest(Long id, CRequestRecord model, boolean ignoreDuplicity) {
		super();
		this.id = id;
		this.model = model;
		this.ignoreDuplicity = ignoreDuplicity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
