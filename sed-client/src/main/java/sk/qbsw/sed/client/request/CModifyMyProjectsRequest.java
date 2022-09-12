package sk.qbsw.sed.client.request;

public class CModifyMyProjectsRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Long projectId;

	private boolean flagMyProject;

	private Long userId;

	public CModifyMyProjectsRequest() {
		super();
	}

	public CModifyMyProjectsRequest(Long projectId, boolean flagMyProject, Long userId) {
		super();
		this.projectId = projectId;
		this.flagMyProject = flagMyProject;
		this.userId = userId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public boolean isFlagMyProject() {
		return flagMyProject;
	}

	public void setFlagMyProject(boolean flagMyProject) {
		this.flagMyProject = flagMyProject;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
