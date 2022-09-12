package sk.qbsw.sed.server.model.codelist;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model mapped to table public.t_ct_request_type
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
@Entity
@Table(schema = "public", name = "t_ct_request_type")
public class CRequestType implements Serializable {
	
	@Column(name = "c_description", nullable = true)
	private String description;

	@Id
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_msg_code", nullable = false)
	private String msgCode;

	@Column(name = "c_code", nullable = false)
	private String code;

	@Column(name = "c_flag_allows_gen_ts", nullable = false)
	private Boolean allowsGenerateTimestamps;

	@Column(name = "fk_activity", nullable = true)
	private Long activityId;

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getAllowsGenerateTimestamps() {
		return allowsGenerateTimestamps;
	}

	public void setAllowsGenerateTimestamps(Boolean allowsGenerateTimestamps) {
		this.allowsGenerateTimestamps = allowsGenerateTimestamps;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}
}
