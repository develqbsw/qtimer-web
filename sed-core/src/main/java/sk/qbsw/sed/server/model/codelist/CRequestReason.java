package sk.qbsw.sed.server.model.codelist;

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

import sk.qbsw.sed.client.model.codelist.CRequestReasonRecord;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * Model mapped to table public.t_reason_request
 * 
 * @author rosenberg
 * @version 1.6.6.2
 * @since 1.6.6.2
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_request_reason", sequenceName = "s_request_reason", allocationSize = 1)
@Table(schema = "public", name = "t_request_reason")
public class CRequestReason implements Serializable {
	
	@Id
	@GeneratedValue(generator = "s_request_reason", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_request_type", nullable = false)
	private CRequestType requestType;

	@Column(name = "c_code", nullable = false, length = 20)
	private String code;

	@Column(name = "c_name", nullable = false, length = 200)
	private String reasonName;

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	@Column(name = "c_flag_system", nullable = false)
	private Boolean system;

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

	public CClient getClient() {
		return client;
	}

	public void setClient(CClient client) {
		this.client = client;
	}

	public CRequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(CRequestType requestType) {
		this.requestType = requestType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getReasonName() {
		return reasonName;
	}

	public void setReasonName(String reasonName) {
		this.reasonName = reasonName;
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

	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}

	public CRequestReasonRecord convert() {
		CRequestReasonRecord record = new CRequestReasonRecord();

		record.setClientId(client.getId());
		record.setCode(code);
		record.setId(id);
		record.setReasonName(reasonName);
		record.setRequestTypeDescription(requestType.getDescription());
		record.setRequestTypeId(requestType.getId());
		record.setSystem(system);
		record.setValid(valid);

		return record;
	}
}
