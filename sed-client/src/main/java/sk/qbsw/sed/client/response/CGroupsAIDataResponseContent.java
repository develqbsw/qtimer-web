package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.restriction.CGroupsAIData;

public class CGroupsAIDataResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CGroupsAIData groupsAIData;

	public CGroupsAIData getGroupsAIData() {
		return groupsAIData;
	}

	public void setGroupsAIData(CGroupsAIData groupsAIData) {
		this.groupsAIData = groupsAIData;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
