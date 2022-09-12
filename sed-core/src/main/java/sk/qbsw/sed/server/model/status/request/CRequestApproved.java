package sk.qbsw.sed.server.model.status.request;

import sk.qbsw.sed.client.model.IRequestStates;
import sk.qbsw.sed.client.ui.localization.CClientExceptionsMessages;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public class CRequestApproved implements IRequestStatus {

	public Long approve() throws CBusinessException {
		throw new CBusinessException(CClientExceptionsMessages.STATUS_UNALLOWED_TRANSITION);
	}

	public Long cancel() throws CBusinessException {
		return IRequestStates.ID_CANCELLED;
	}

	public Long reject() throws CBusinessException {
		return IRequestStates.ID_REJECTED;
	}

	public void modify() throws CBusinessException {
		throw new CBusinessException(CClientExceptionsMessages.STATUS_CANNOT_MODIFY);
	}
}
