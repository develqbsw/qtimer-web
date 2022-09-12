package sk.qbsw.sed.client.model.brw;

import java.io.Serializable;

import sk.qbsw.sed.client.model.abstractclass.ADataForEditableColumn;

@SuppressWarnings("serial")
public class CUserProjectRecord extends ADataForEditableColumn implements Serializable {

	private Long projectId;
	private String projectName;
	private Boolean flagMyProject;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Boolean getFlagMyProject() {
		return flagMyProject;
	}

	public void setFlagMyProject(Boolean flagMyProject) {
		this.flagMyProject = flagMyProject;
	}

	@Override
	public Boolean isActive() {
		return getFlagMyProject();
	}
}
