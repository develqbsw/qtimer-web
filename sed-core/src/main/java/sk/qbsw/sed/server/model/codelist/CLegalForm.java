package sk.qbsw.sed.server.model.codelist;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model mapped to table public.t_ct_legal_form
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
@Entity
@Table(schema = "public", name = "t_ct_legal_form")
public class CLegalForm implements Serializable {
	
	@Column(name = "c_description", nullable = true)
	private String description;

	@Id
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_msg_code", nullable = false)
	private String msgCode;

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	public String getDescription() {
		return description;
	}

	public Long getId() {
		return id;
	}

	public String getMsgCode() {
		return msgCode;
	}

	public Boolean getValid() {
		return valid;
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

	public void setValid(Boolean valid) {
		this.valid = valid;
	}
}
