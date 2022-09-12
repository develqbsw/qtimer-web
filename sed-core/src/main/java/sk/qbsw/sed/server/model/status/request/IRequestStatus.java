package sk.qbsw.sed.server.model.status.request;

import sk.qbsw.sed.framework.security.exception.CBusinessException;

public interface IRequestStatus {
	
	public Long approve() throws CBusinessException;

	public Long reject() throws CBusinessException;

	public Long cancel() throws CBusinessException;

	public void modify() throws CBusinessException;
}
