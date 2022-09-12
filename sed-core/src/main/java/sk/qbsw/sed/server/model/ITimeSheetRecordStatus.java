package sk.qbsw.sed.server.model;

import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface ITimeSheetRecordStatus {

	public Long confirmedByEmployee() throws CBusinessException;

	public void modify() throws CBusinessException;

	public void delete() throws CBusinessException;
}
