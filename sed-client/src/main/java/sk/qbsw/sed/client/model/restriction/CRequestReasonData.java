package sk.qbsw.sed.client.model.restriction;

import java.io.Serializable;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

/**
 * Data object for client server communication
 * 
 * @author rosenberg
 * @version 1.6.6.2
 * @since 1.6.6.2
 */
@SuppressWarnings("serial")
public class CRequestReasonData implements Serializable {
	Long id;

	Long clientId;

	Long requestTypeId;

	String code;

	String name;

	Boolean valid;

	Boolean flagSystem;

	public CCodeListRecord getRequestType() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(requestTypeId);
		return record;
	}

	public void setRequestType(CCodeListRecord record) {
		this.requestTypeId = record.getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getRequestTypeId() {
		return requestTypeId;
	}

	public void setRequestTypeId(Long requestTypeId) {
		this.requestTypeId = requestTypeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Boolean getFlagSystem() {
		return flagSystem;
	}

	public void setFlagSystem(Boolean flagSystem) {
		this.flagSystem = flagSystem;
	}
}
