package sk.qbsw.sed.client.model.codelist;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CCodeChangeModel implements Serializable {

	Long userId;
	String code;

	public CCodeListRecord getUser() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(userId);
		return record;
	}

	public void setUser(CCodeListRecord record) {
		this.userId = record.getId();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
