package sk.qbsw.sed.server.model.codelist;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import sk.qbsw.sed.client.model.ITimeSheetRecordStates;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.model.ITimeSheetRecordStatus;
import sk.qbsw.sed.server.model.status.timesheetrecord.CTimeSheetRecordConfirmedByAdmin;
import sk.qbsw.sed.server.model.status.timesheetrecord.CTimeSheetRecordConfirmedByEmployee;
import sk.qbsw.sed.server.model.status.timesheetrecord.CTimeSheetRecordConfirmedBySuperior;
import sk.qbsw.sed.server.model.status.timesheetrecord.CTimeSheetRecordNew;

/**
 * Model mapped to table public.t_ct_tsr_status
 * 
 * @author Pavol Lobb
 * @version 1.6.8.4
 * @since 1.6.8.4
 */
@SuppressWarnings("serial")
@Entity
@Table(schema = "public", name = "t_ct_tsr_status")
public class CTimeSheetRecordStatus implements Serializable, ITimeSheetRecordStatus {
	
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

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMsgCode() {
		return msgCode;
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
	private ITimeSheetRecordStatus getStatusObject() throws CBusinessException {
		if (ITimeSheetRecordStates.ID_NEW.equals(getId())) {
			return new CTimeSheetRecordNew();
		} else if (ITimeSheetRecordStates.ID_CONFIRMED_BY_EMPLOYEE.equals(getId())) {
			return new CTimeSheetRecordConfirmedByEmployee();
		} else if (ITimeSheetRecordStates.ID_CONFIRMED_BY_SUPERIOR.equals(getId())) {
			return new CTimeSheetRecordConfirmedBySuperior();
		} else if (ITimeSheetRecordStates.ID_CONFIRMED_BY_ADMIN.equals(getId())) {
			return new CTimeSheetRecordConfirmedByAdmin();
		} else {
			throw new CBusinessException("unknown state");
		}
	}

	public Long confirmedByEmployee() throws CBusinessException {
		return getStatusObject().confirmedByEmployee();
	}

	public void modify() throws CBusinessException {
		getStatusObject().modify();
	}

	public void delete() throws CBusinessException {
		getStatusObject().delete();
	}

	@Override
	public String toString() {
		return description;
	}
}
