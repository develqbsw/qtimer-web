package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CResultProjectsGroups implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<CCodeListRecord> projectGroups;

	public CResultProjectsGroups() {
		projectGroups = new ArrayList<>();
	}

	public List<CCodeListRecord> getProjectGroups() {
		return projectGroups;
	}

	public void setProjectGroups(List<CCodeListRecord> projectGroups) {
		this.projectGroups = projectGroups;
	}
}
