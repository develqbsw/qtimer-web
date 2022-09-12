package sk.qbsw.sed.client.model.timestamp;

import java.util.Date;

import sk.qbsw.sed.client.model.table.IFilterCriteria;

/**
 * 
 * @author rosenberg
 *
 */
public interface IMyTimesheetGenerateBrwFilterCriteria extends IFilterCriteria {
	
	public Date getDateFrom();

	public Date getDateTo();

	public String getDataInputType();

	public Boolean getValidData();
}
