package sk.qbsw.sed.server.model.params;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import sk.qbsw.sed.client.model.params.CLockDateRecord;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * Model mapped to table public.t_time_sheet_lock_date
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.0
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_lock_date", sequenceName = "s_lock_date", allocationSize = 1)
@Table(schema = "public", name = "t_lock_date")
public class CLockDate implements Serializable {
	
	@Id
	@GeneratedValue(generator = "s_lock_date", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_owner", nullable = false)
	private CUser owner;

	@Column(name = "c_date_ts_user_validate_to", nullable = false)
	private Calendar userTimestampValidTo;

	@Column(name = "c_date_rq_user_validate_to", nullable = false)
	private Calendar userRequestValidTo;

	@Column(name = "c_date_ts_locked_to", nullable = false)
	private Calendar timestampLockTo;

	@Column(name = "c_date_rq_locked_to", nullable = false)
	private Calendar requestLockTo;

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_changedby", nullable = false)
	private CUser changedBy;

	@Column(name = "c_datetime_changed", nullable = false)
	private Calendar changeTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CUser getOwner() {
		return owner;
	}

	public void setOwner(CUser owner) {
		this.owner = owner;
	}

	public CClient getClient() {
		return client;
	}

	public void setClient(CClient client) {
		this.client = client;
	}

	public Calendar getUserTimestampValidTo() {
		return userTimestampValidTo;
	}

	public void setUserTimestampValidTo(Calendar userTimestampValidTo) {
		this.userTimestampValidTo = userTimestampValidTo;
	}

	public Calendar getUserRequestValidTo() {
		return userRequestValidTo;
	}

	public void setUserRequestValidTo(Calendar userRequestValidTo) {
		this.userRequestValidTo = userRequestValidTo;
	}

	public Calendar getTimestampLockTo() {
		return timestampLockTo;
	}

	public void setTimestampLockTo(Calendar timestampLockTo) {
		this.timestampLockTo = timestampLockTo;
	}

	public Calendar getRequestLockTo() {
		return requestLockTo;
	}

	public void setRequestLockTo(Calendar requestLockTo) {
		this.requestLockTo = requestLockTo;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public CUser getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(CUser changedBy) {
		this.changedBy = changedBy;
	}

	public Calendar getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Calendar changeTime) {
		this.changeTime = changeTime;
	}

	public CLockDateRecord convert() {
		CLockDateRecord record = new CLockDateRecord();

		record.setChangedByName(changedBy.getName());
		record.setChangedBySurname(changedBy.getSurname());
		record.setChangeTime(changeTime.getTime());
		record.setId(id);
		record.setOwnerId(owner.getId());
		record.setOwnerName(owner.getName());
		record.setOwnerSurname(owner.getSurname());
		record.setRequestLockTo(requestLockTo.getTime());
		record.setTimestampLockTo(timestampLockTo.getTime());
		record.setUserRequestValidTo(userRequestValidTo.getTime());
		record.setUserTimestampValidTo(userTimestampValidTo.getTime());
		record.setValid(valid);

		return record;
	}
}
