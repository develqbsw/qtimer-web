package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.codelist.CResultProjectsGroups;

public class CResultProjectsGroupsResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CResultProjectsGroups resultProjectsGroups;

	public CResultProjectsGroups getResultProjectsGroups() {
		return resultProjectsGroups;
	}

	public void setResultProjectsGroups(CResultProjectsGroups resultProjectsGroups) {
		this.resultProjectsGroups = resultProjectsGroups;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
