package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
import sk.qbsw.sed.client.model.timestamp.ChooseDefaultTimestampFormModelToChange;

public class CBrwTimeStampMassChangeRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	protected CSubrodinateTimeStampBrwFilterCriteria criteria;
	protected ChooseDefaultTimestampFormModelToChange formModel;

	public CBrwTimeStampMassChangeRequest() {
		super();
	}

	public CBrwTimeStampMassChangeRequest(CSubrodinateTimeStampBrwFilterCriteria criteria, ChooseDefaultTimestampFormModelToChange formModel) {
		super();
		this.criteria = criteria;
		this.formModel = formModel;
	}

	public CSubrodinateTimeStampBrwFilterCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CSubrodinateTimeStampBrwFilterCriteria criteria) {
		this.criteria = criteria;
	}

	public ChooseDefaultTimestampFormModelToChange getFormModel() {
		return formModel;
	}

	public void setFormModel(ChooseDefaultTimestampFormModelToChange formModel) {
		this.formModel = formModel;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
