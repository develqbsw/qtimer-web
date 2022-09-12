package sk.qbsw.sed.client.model.codelist;

/**
 * Top browser project filter object
 * 
 * @author rosenberg
 *
 */
@SuppressWarnings("serial")
public class CProjectBrwFilterCriteria implements IProjectBrwFilterCriteria {

	private Long projectId;

	private String projectCode;

	private String projectGroup;

	private String projectName;

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@Override
	public Long getProjectId() {
		return this.projectId;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	@Override
	public String getProjectCode() {
		return this.projectCode;
	}

	public void setProjectGroup(String projectGroup) {
		this.projectGroup = projectGroup;
	}

	@Override
	public String getProjectGroup() {
		return this.projectGroup;
	}

	@Override
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
