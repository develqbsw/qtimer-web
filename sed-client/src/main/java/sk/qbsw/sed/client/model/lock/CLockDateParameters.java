package sk.qbsw.sed.client.model.lock;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Ladislav Rosenberg
 * @version 1.0
 * @since 1.6.0
 */
@SuppressWarnings("serial")
public class CLockDateParameters implements Serializable {
	/**
	 * 0: all </br>
	 * 1: time stamps </br>
	 * 2: requests</br>
	 */
	private Integer recordType;

	private List<Long> recordIds;

	private Date lockDate;

	/**
	 * 0: all </br>
	 * 1: time stamps </br>
	 * 2: requests</br>
	 */
	public Integer getRecordType() {
		return recordType;
	}

	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}

	public List<Long> getRecordIds() {
		return recordIds;
	}

	public void setRecordIds(List<Long> recordIds) {
		this.recordIds = recordIds;
	}

	public Date getLockDate() {
		return lockDate;
	}

	public void setLockDate(Date lockDate) {
		this.lockDate = lockDate;
	}
}
