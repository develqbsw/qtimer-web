package sk.qbsw.sed.client.model.params;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class CLockDateRecord implements Serializable {
	private Long id;

	private Long ownerId;

	private String ownerName;

	private String ownerSurname;

	private Date userTimestampValidTo;

	private Date userRequestValidTo;

	private Date timestampLockTo;

	private Date requestLockTo;

	private Boolean valid;

	private String changedByName;

	private String changedBySurname;

	private Date changeTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerSurname() {
		return ownerSurname;
	}

	public void setOwnerSurname(String ownerSurname) {
		this.ownerSurname = ownerSurname;
	}

	public Date getUserTimestampValidTo() {
		return userTimestampValidTo;
	}

	public void setUserTimestampValidTo(Date userTimestampValidTo) {
		this.userTimestampValidTo = userTimestampValidTo;
	}

	public Date getUserRequestValidTo() {
		return userRequestValidTo;
	}

	public void setUserRequestValidTo(Date userRequestValidTo) {
		this.userRequestValidTo = userRequestValidTo;
	}

	public Date getTimestampLockTo() {
		return timestampLockTo;
	}

	public void setTimestampLockTo(Date timestampLockTo) {
		this.timestampLockTo = timestampLockTo;
	}

	public Date getRequestLockTo() {
		return requestLockTo;
	}

	public void setRequestLockTo(Date requestLockTo) {
		this.requestLockTo = requestLockTo;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public String getChangedByName() {
		return changedByName;
	}

	public void setChangedByName(String changedByName) {
		this.changedByName = changedByName;
	}

	public String getChangedBySurname() {
		return changedBySurname;
	}

	public void setChangedBySurname(String changedBySurname) {
		this.changedBySurname = changedBySurname;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}
}
