package sk.qbsw.sed.client.model.timestamp;

import java.util.Date;

import sk.qbsw.sed.client.model.table.IFilterCriteria;

public interface ITimeStampBrwFilterCriteria extends IFilterCriteria {

	public Long getActivityId();

	public Long getProjectId();

	public Date getDateFrom();

	public Date getDateTo();

	public String getSearchText();
}
