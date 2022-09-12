package sk.qbsw.sed.server.service.report;

public class CAlertInteracNote {
	
	private String activityName;
	private String projectName;

	public CAlertInteracNote(String activityName, String projectName) {
		super();
		this.activityName = activityName;
		this.projectName = projectName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityName == null) ? 0 : activityName.hashCode());
		result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CAlertInteracNote other = (CAlertInteracNote) obj;
		if (activityName == null) {
			if (other.activityName != null) {
				return false;
			}
		} else if (!activityName.equals(other.activityName)) {
			return false;
		}
		if (projectName == null) {
			if (other.projectName != null) {
				return false;
			}
		} else if (!projectName.equals(other.projectName)) {
			return false;
		}
		return true;
	}
}
