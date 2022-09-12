package sk.qbsw.sed.client.model.codelist;

import sk.qbsw.sed.client.model.table.IFilterCriteria;

public interface IActivityBrwFilterCriteria extends IFilterCriteria {

	public Long getActivityId();

	public String getActivityName();
}
