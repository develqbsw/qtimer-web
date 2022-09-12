package sk.qbsw.sed.server.model.codelist;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import sk.qbsw.sed.client.model.IRequestStates;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.model.status.request.CRequestApproved;
import sk.qbsw.sed.server.model.status.request.CRequestCancelled;
import sk.qbsw.sed.server.model.status.request.CRequestCreated;
import sk.qbsw.sed.server.model.status.request.CRequestRejected;
import sk.qbsw.sed.server.model.status.request.IRequestStatus;

/**
 * Model mapped to table public.t_ct_request_status
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
@Entity
@Table(schema = "public", name = "t_ct_request_status")
public class CRequestStatus implements Serializable, IRequestStatus {
	
	@Column(name = "c_description", nullable = true)
	private String description;

	@Id
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_msg_code", nullable = false)
	private String msgCode;

	public String getDescription() {
		return description;
	}

	public Long getId() {
		return id;
	}

	public String getMsgCode() {
		return msgCode;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}

	/**
	 * Instantiates actual status object
	 * 
	 * @return
	 * @throws CBusinessException
	 */
	private IRequestStatus getStatusObject() throws CBusinessException {
		if (IRequestStates.ID_CREATED.equals(getId())) {
			return new CRequestCreated();
		} else if (IRequestStates.ID_APPROVED.equals(getId())) {
			return new CRequestApproved();
		} else if (IRequestStates.ID_CANCELLED.equals(getId())) {
			return new CRequestCancelled();
		} else if (IRequestStates.ID_REJECTED.equals(getId())) {
			return new CRequestRejected();
		} else {
			throw new CBusinessException("unknown state");
		}
	}

	public Long approve() throws CBusinessException {
		return getStatusObject().approve();
	}

	public Long cancel() throws CBusinessException {
		return getStatusObject().cancel();
	}

	public Long reject() throws CBusinessException {
		return getStatusObject().reject();
	}

	public void modify() throws CBusinessException {
		getStatusObject().modify();
	}
}
