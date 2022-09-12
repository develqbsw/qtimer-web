package sk.qbsw.sed.server.model.status.timesheetrecord;

import sk.qbsw.sed.client.model.ITimeSheetRecordStates;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.model.ITimeSheetRecordStatus;

public class CTimeSheetRecordNew implements ITimeSheetRecordStatus {

	public Long confirmedByEmployee() throws CBusinessException {
		return ITimeSheetRecordStates.ID_CONFIRMED_BY_EMPLOYEE;
	}

	public void modify() throws CBusinessException {
		// do nothing
	}

	public void delete() throws CBusinessException {
		// do nothing
	}
}
