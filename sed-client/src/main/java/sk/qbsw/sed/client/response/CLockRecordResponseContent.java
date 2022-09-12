package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.lock.CLockRecord;

public class CLockRecordResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private CLockRecord lockRecord;

	public CLockRecord getLockRecord() {
		return lockRecord;
	}

	public void setLockRecord(CLockRecord lockRecord) {
		this.lockRecord = lockRecord;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
