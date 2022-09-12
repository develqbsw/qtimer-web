package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.codelist.CActivityRecord;

public class CActivityRecordResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CActivityRecord activityRecord;

	public CActivityRecord getActivityRecord() {
		return activityRecord;
	}

	public void setActivityRecord(CActivityRecord activityRecord) {
		this.activityRecord = activityRecord;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
