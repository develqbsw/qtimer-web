package sk.qbsw.sed.server.model.status.timesheetrecord;

import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.model.ITimeSheetRecordStatus;

public class CTimeSheetRecordConfirmedBySuperior implements ITimeSheetRecordStatus {

	public Long confirmedByEmployee() throws CBusinessException {
		throw new CBusinessException(CClientExceptionsMessages.STATUS_UNALLOWED_TRANSITION);
	}

	public void modify() throws CBusinessException {
		throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_RECORD_CANNOT_MODIFY);
	}

	@Override
	public void delete() throws CBusinessException {
		throw new CBusinessException(CClientExceptionsMessages.TIMESHEET_RECORD_CANNOT_DELETE);
	}
}
