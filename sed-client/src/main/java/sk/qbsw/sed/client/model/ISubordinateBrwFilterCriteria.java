package sk.qbsw.sed.client.model;

import java.util.Set;

import sk.qbsw.sed.client.model.table.IFilterCriteria;

public interface ISubordinateBrwFilterCriteria extends IFilterCriteria {

	public Set<Long> getEmplyees();

	public void setEmplyees(final Set<Long> emplyees);
}
