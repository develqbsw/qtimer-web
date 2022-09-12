package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CRequestReasonRecord implements Serializable {
	
	private Long id;
	private Long clientId;
	private Long requestTypeId;
	private String requestTypeDescription;
	private String code;
	private String reasonName;
	private Boolean valid;
	private Boolean system;

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

	public String getRequestTypeDescription() {
		return requestTypeDescription;
	}

	public void setRequestTypeDescription(String requestTypeDescription) {
		this.requestTypeDescription = requestTypeDescription;
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

	public Boolean getSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}
}
