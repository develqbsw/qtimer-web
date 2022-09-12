package sk.qbsw.sed.client.request;

import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;

public class CGetSumAndAverageTimeForUsersRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CSubrodinateTimeStampBrwFilterCriteria filter;

	public CGetSumAndAverageTimeForUsersRequest(CSubrodinateTimeStampBrwFilterCriteria filter) {
		this.filter = filter;
	}

	public CSubrodinateTimeStampBrwFilterCriteria getFilter() {
		return filter;
	}

	public void setFilter(CSubrodinateTimeStampBrwFilterCriteria filter) {
		this.filter = filter;
	}

	@Override
	public Boolean validate() {
		return null;
	}
}
