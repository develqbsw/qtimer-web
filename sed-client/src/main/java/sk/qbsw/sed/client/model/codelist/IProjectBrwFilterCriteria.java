package sk.qbsw.sed.client.model.codelist;

import sk.qbsw.sed.client.model.table.IFilterCriteria;

/**
 * 
 * @author rosenberg
 *
 */
public interface IProjectBrwFilterCriteria extends IFilterCriteria {
	
	public String getProjectCode();

	public Long getProjectId();

	public String getProjectGroup();

	public String getProjectName();
}
