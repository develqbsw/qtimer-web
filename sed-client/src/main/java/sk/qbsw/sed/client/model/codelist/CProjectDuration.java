package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CProjectDuration implements Serializable {
	private Long projectId;
	private String projectName;
	private Long duration;

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

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}
}
