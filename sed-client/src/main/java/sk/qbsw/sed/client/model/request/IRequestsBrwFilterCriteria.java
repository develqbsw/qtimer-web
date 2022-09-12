package sk.qbsw.sed.client.model.request;

import java.util.Date;

import sk.qbsw.sed.client.model.table.IFilterCriteria;

public interface IRequestsBrwFilterCriteria extends IFilterCriteria {

	public Long getStateId();

	public Long getTypeId();

	public Date getDateFrom();

	public Date getDateTo();
}
