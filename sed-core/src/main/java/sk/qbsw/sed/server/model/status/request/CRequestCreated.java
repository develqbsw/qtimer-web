package sk.qbsw.sed.server.model.status.request;

import sk.qbsw.sed.client.model.IRequestStates;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

public class CRequestCreated implements IRequestStatus {

	public Long approve() throws CBusinessException {
		return IRequestStates.ID_APPROVED;
	}

	public Long cancel() throws CBusinessException {
		return IRequestStates.ID_CANCELLED;
	}

	public Long reject() throws CBusinessException {
		return IRequestStates.ID_REJECTED;
	}

	public void modify() throws CBusinessException {
		// Auto-generated method stub
	}
}
