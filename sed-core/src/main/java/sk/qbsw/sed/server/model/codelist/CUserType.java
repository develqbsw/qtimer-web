package sk.qbsw.sed.server.model.codelist;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model mapped to table public.t_ct_user_type
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@Entity
@Table(schema = "public", name = "t_ct_user_type")
public class CUserType {
	
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
}
