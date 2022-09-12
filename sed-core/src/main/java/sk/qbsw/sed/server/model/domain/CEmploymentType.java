package sk.qbsw.sed.server.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model mapped to table public.t_ct_type_of_employment
 * 
 * @author Ľudovít Kováč
 */
@Entity
@Table(schema = "public", name = "t_ct_type_of_employment")
public class CEmploymentType {

	@Id
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_msg_code", nullable = false)
	private String msgCode;

	@Column(name = "c_description", nullable = true)
	private String description;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
