package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;

/**
 * 
 * @author moravcik
 *
 */
public class CActivityIntervalDataResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CActivityIntervalData activityIntervalData;

	public CActivityIntervalData getActivityIntervalData() {
		return activityIntervalData;
	}

	public void setActivityIntervalData(CActivityIntervalData activityIntervalData) {
		this.activityIntervalData = activityIntervalData;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
